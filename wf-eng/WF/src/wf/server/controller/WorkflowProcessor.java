package wf.server.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.log4j.Logger;
import org.jaxen.JaxenException;
import org.xml.sax.SAXException;
import wf.cfg.Configuratii;
import wf.db.Persistence;
import wf.exceptions.ExceptieWF;
import wf.jms.PublisherEvenimente;
import wf.jms.JMSPublisher;
import wf.jms.MessageProperty;
import wf.model.DirectedGraph;
import wf.model.ItemModel;
import wf.model.Nod;
import wf.model.StareWF;
import wf.server.util.PopNode;
import wf.xml.DefinitionParser;

public class WorkflowProcessor {

    @SuppressWarnings("unchecked")
    private static List activeWorkflows;
    private static DirectedGraphP directedGraphP = Persistence
	    .getDirectGraphP();
    private static PublisherEvenimente eventsPublisher = new PublisherEvenimente();
    @SuppressWarnings("unchecked")
    private static Map graphsByGraphId = new HashMap();
    @SuppressWarnings("unchecked")
    private static Map graphsByNameAndVersion = new HashMap();
    private static Object guard = new Object();
    private static InboxP inboxP = Persistence.getInboxP();
    static WorkflowProcessor instance;
    private static Logger log = Logger.getLogger(WorkflowProcessor.class);
    public static final int MODE_LOCAL = 1;
    public static final int MODE_SERVER = 0;

    private static ProcessStack processStack = Persistence.getProcessStack();
    @SuppressWarnings("unchecked")
    private static List suspendedWorkflows;

    private static WaitingP waitingP = Persistence.getWaitingP();

    private static WorkflowP workflowP = Persistence.getWorkflowP();

    public static WorkflowProcessor getInstance() throws SQLException {
	synchronized (guard) {
	    if (instance == null) {
		instance = (WorkflowProcessor) Persistence
			.enhanceInstanceOfClass(WorkflowProcessor.class);
		instance.init();
	    }
	    return instance;
	}
    }

    private int mode = MODE_SERVER;

    public void abortWorkflow(final Integer wfId, final String user)
	    throws ExceptieWF, SQLException {

	int graphId = workflowP.getGraphId(wfId);
	DirectedGraph dg = this.getGraphByGraphId(graphId);

	Integer key = wfId;
	activeWorkflows.remove(key);
	suspendedWorkflows.remove(key);
	workflowP.abortWorkflow(wfId);

	inboxP.removeWorkItems(wfId);
	waitingP.removeProcesses(wfId);

	try {
	    String workflowName = dg.getName();
	    int version = dg.getVersion();
	    eventsPublisher.publishWorkflowAbortedEvent(workflowName, version,
		    wfId, user);
	} catch (ExceptieWF e) {
	    log.warn("Failed to publish event");
	}
    }

    public void completeWorkItem(final String workflowName,
	    final int workflowVersion, final String processName,
	    final ItemModel witem) throws ExceptieWF, SQLException,
	    JaxenException, IOException, ParserConfigurationException,
	    SAXException {
	log.info("In CompleteWorkItem.");
	log.info("workflowName: " + workflowName);
	log.info("processName: " + processName);

	log.info(" Validating Work Item: " + witem);

	if ((witem == null) || (witem.getId() == null)) {
	    throw new ExceptieWF(
		    "Cannot complete work item. Null workitem or its ID");
	}

	@SuppressWarnings("unused")
	Integer wid = witem.getId();
	Integer wfId = witem.getWorkflowId();
	if (wfId == null) {
	    throw new ExceptieWF("Cannot complete work item. Null workflow Id");
	}

	Integer key = wfId;

	if (!activeWorkflows.contains(key)) {
	    return;
	}

	if (suspendedWorkflows.contains(key)) {
	    throw new ExceptieWF(
		    "Cannot complete work item. Workflow instance has been suspended");
	}

	int gid = -1;
	try {
	    gid = directedGraphP.getGraphId(workflowName, workflowVersion)
		    .intValue();
	} catch (Exception e) {
	    throw new ExceptieWF(e);
	}

	if (!inboxP.isWorkItemValid(gid, processName, witem)) {
	    throw new ExceptieWF(
		    "Cannot complete work item. Invalid work item state.");
	}

	log.info("workflow ID: " + wfId);

	log
		.info("Work Item passed validation. Now attempting to complete work item");

	DirectedGraph dg = this.getGraphByGraphId(gid);
	Nod rootNode = dg.getRootNode();
	Nod thisNode = rootNode.getNode(processName);

	this
		.transitionFrom(gid, workflowName, workflowVersion, thisNode,
			witem);

    }

    @SuppressWarnings("unchecked")
    public void deployModel(final String xml, final String type,
	    final String user) throws ExceptieWF {

	DirectedGraph dg = null;

	if (WorkflowEngine.FLOW_TYPE_WF.equals(type)) {
	    try {
		dg = DefinitionParser.parse(xml);
		dg.validate();
	    } catch (Exception e) {
		throw new ExceptieWF("Failed to parse XML : " + e.getMessage(),
			e);
	    }
	    try {
		dg.saveDB();
	    } catch (Exception e) {
		throw new ExceptieWF("Failed to save model to database ", e);
	    }

	    String modelName = dg.getName();
	    int graphId = dg.getGraphId();
	    log.info("Saved model: " + modelName);
	    log.info("graphId is: " + graphId);
	    graphsByGraphId.put(new Integer(graphId), dg);
	    String nameVers = dg.getName() + dg.getVersion();
	    graphsByNameAndVersion.put(nameVers, dg);

	    try {
		eventsPublisher.publishModelDeployedEvent(modelName, dg
			.getVersion(), user);
	    } catch (Throwable e) {
		log.warn("Failed to publish event");
	    }
	} else {
	    throw new ExceptieWF("Type: " + type + " is not supported.");
	}
    }

    private boolean evaluateRule(final ItemModel witem, final String rule)
	    throws ExceptieWF, JaxenException, IOException,
	    ParserConfigurationException, SAXException {

	boolean result = true;
	if ((rule != null) && !rule.equals("") && !rule.equals("always")) {
	    log.info("Evaluating rule: " + rule);
	    if (RuleEngine.evaluate(witem, rule) == false) {
		result = false;
	    }
	    log.info("Rule: " + rule + " evaluated to: " + result);
	}
	return result;
    }

    @SuppressWarnings("unchecked")
    public List getActiveWorkflows() throws ExceptieWF, SQLException {
	List v = new ArrayList();
	StareWF ws = null;
	List wflowIds = workflowP.getActiveWorkflows();
	Iterator itr = wflowIds.iterator();
	while (itr.hasNext()) {
	    Integer wfId = (Integer) itr.next();
	    ws = this.getWorkflowState(wfId);
	    v.add(ws);
	}
	return v;
    }

    @SuppressWarnings("unchecked")
    public List getAllWorkflows() throws ExceptieWF {
	try {
	    List v = new ArrayList();
	    StareWF ws = null;

	    List wflowIds = workflowP.getAllWorkflows();
	    Iterator itr = wflowIds.iterator();
	    while (itr.hasNext()) {
		Integer wfId = (Integer) itr.next();
		ws = this.getWorkflowState(wfId);
		v.add(ws);
	    }
	    return v;
	} catch (Exception e) {
	    throw new ExceptieWF(e);
	}
    }

    @SuppressWarnings("unchecked")
    private DirectedGraph getGraphByGraphId(final int gid) throws ExceptieWF {
	DirectedGraph dg = (DirectedGraph) graphsByGraphId
		.get(new Integer(gid));
	if (dg == null) {
	    try {
		dg = DirectedGraph.loadByGraphId(gid);
		graphsByGraphId.put(new Integer(gid), dg);
		String nameVers = dg.getName() + dg.getVersion();
		graphsByNameAndVersion.put(nameVers, dg);
	    } catch (Exception e) {
		throw new ExceptieWF(e);
	    }
	}
	return dg;
    }

    @SuppressWarnings("unchecked")
    private DirectedGraph getGraphByNameAndVersion(final String name,
	    final int version) throws ExceptieWF, SQLException {
	DirectedGraph dg = (DirectedGraph) graphsByNameAndVersion.get(name
		+ version);
	if (dg == null) {
	    log.info("Loading workflow: " + name + " " + version);
	    dg = directedGraphP.loadDirectedGraph(name, version);

	    int gid = dg.getGraphId();
	    graphsByGraphId.put(new Integer(gid), dg);
	    String nameVers = dg.getName() + dg.getVersion();
	    graphsByNameAndVersion.put(nameVers, dg);
	}
	return dg;
    }

    public int getMode() {
	return this.mode;
    }

    @SuppressWarnings("unchecked")
    public List getModels() throws SQLException {
	return workflowP.getModels();
    }

    public ItemModel getNextWorkItem(final String wfName, final String procName)
	    throws ExceptieWF, SQLException {
	return Persistence.getWorkItemP().getNextWorkItem(wfName, procName);
    }

    public Nod getNodeByName(final String workflowName,
	    final int workflowVersion, final String nodeName)
	    throws ExceptieWF, SQLException {
	Nod node = null;
	DirectedGraph dg = this.getGraphByNameAndVersion(workflowName,
		workflowVersion);
	if (dg != null) {
	    node = dg.getNode(nodeName);
	} else {
	    throw new ExceptieWF("Can't find graph for " + workflowName
		    + ", version: " + workflowVersion);
	}
	return node;
    }

    @SuppressWarnings("unchecked")
    public List getProcessNodes(final Integer wfId) throws SQLException,
	    ExceptieWF {

	int gid = workflowP.getGraphId(wfId);
	DirectedGraph dg = this.getGraphByGraphId(gid);
	List nodes = null;
	if (dg != null) {
	    nodes = dg.getNodes(Nod.PROCESS);
	} else {
	    throw new ExceptieWF("Can't find graph for workflow id: " + wfId);
	}
	return nodes;
    }

    public Object getVariable(final Integer workflowId, final String name)
	    throws SQLException {
	return workflowP.getVariable(workflowId, name);
    }

    @SuppressWarnings("unchecked")
    public List getWorkflowsByName(final String name) throws ExceptieWF,
	    SQLException {
	List v = new ArrayList();
	StareWF ws = null;
	List wflowIds = workflowP.getWorkflowsByName(name);
	Iterator itr = wflowIds.iterator();
	while (itr.hasNext()) {
	    Integer wfId = (Integer) itr.next();
	    ws = this.getWorkflowState(wfId);
	    v.add(ws);
	}
	return v;
    }

    public StareWF getWorkflowState(final Integer wfId) throws ExceptieWF,
	    SQLException {
	return workflowP.getWorkflowState(wfId);
    }

    public ItemModel getWorkItem(final Integer wid, final String procName)
	    throws ExceptieWF, SQLException {
	return Persistence.getWorkItemP().getWorkItem(wid, procName);
    }

    @SuppressWarnings("unchecked")
    public List getWorkItems(final String wfName, final String procName)
	    throws ExceptieWF, SQLException {
	return Persistence.getWorkItemP().getWorkItems(wfName, procName);
    }

    void init() throws SQLException {
	activeWorkflows = workflowP.getActiveWorkflows();
	suspendedWorkflows = workflowP.getSuspendedWorkflows();

    }

    private boolean orTransitionHasOccurred(final Integer workflowId,
	    final int nodeId) throws SQLException {
	return workflowP.existsOrtab(workflowId, nodeId);
    }

    private void processContainer(final int gid, final Nod containerNode,
	    final ItemModel witem) throws ExceptieWF, SQLException,
	    JaxenException, IOException, ParserConfigurationException,
	    SAXException {

	log.info("in processContainer");

	Integer wfId = witem.getWorkflowId();
	String containee = containerNode.getContainee();
	int containeeVersion = containerNode.getContaineeVersion();

	log.info("containee name: " + containee);
	log.info("containee version: " + containeeVersion);

	DirectedGraph dg = this.getGraphByNameAndVersion(containee,
		containeeVersion);
	int containeeGid = dg.getGraphId();

	log.info("Successfully loaded graph");

	int graphId = dg.getGraphId();
	Nod startNode = dg.getRootNode();
	Nod endNode = dg.getEndNode();

	log.info("graphId: " + graphId);
	log.info("startNode: " + endNode);
	log.info("endNode: " + endNode);
	if (containerNode.getDestinations().size() == 0) {
	    log.info("Starting containee workflow: " + containee + " version: "
		    + containeeVersion);
	    this.startContaineeWorkflow(containee, containeeVersion, witem,
		    "System", wfId);
	} else {
	    processStack.push(wfId, gid, containerNode, endNode);
	    this.transitionFromStartNode(containeeGid, containee,
		    containeeVersion, startNode, witem);
	}
    }

    private void recordOrTransition(final Integer workflowId, final int nodeId)
	    throws SQLException {
	workflowP.insertOrtab(workflowId, nodeId);
    }

    public void resumeWorkflow(final Integer wfId) throws ExceptieWF,
	    SQLException {

	int graphId = workflowP.getGraphId(wfId);
	DirectedGraph dg = this.getGraphByGraphId(graphId);

	Integer key = wfId;
	if (!suspendedWorkflows.contains(key)) {
	    throw new ExceptieWF("Workflow is not currently suspended");
	}
	suspendedWorkflows.remove(key);
	workflowP.resumeWorkflow(wfId);

	try {
	    String workflowName = dg.getName();
	    int version = dg.getVersion();
	    eventsPublisher.publishWorkflowResumedEvent(workflowName, version,
		    wfId, "system");
	} catch (ExceptieWF e) {
	    log.warn("Failed to publish event");
	}
    }

    @SuppressWarnings("unchecked")
    private void sendInboxNotification(final String workflowName,
	    final String procName, final ItemModel witem) {
	try {
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    ObjectOutputStream s = new ObjectOutputStream(out);
	    s.writeObject(witem);
	    s.flush();
	    byte[] barr = out.toByteArray();

	    List props = new ArrayList();
	    MessageProperty mp = new MessageProperty();
	    mp.name = "ProcessName";
	    mp.value = workflowName + procName;
	    props.add(mp);
	    JMSPublisher.send(Configuratii.getInboxTopic(), barr, props);
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public void setEventsEnabled(boolean enabled) {
	eventsPublisher.setDoNotPublish(!enabled);
    }

    public void setMode(final int mode) {
	this.mode = mode;
    }

    public void setVariable(final Integer workflowId, final String name,
	    final Object value) throws ExceptieWF, SQLException {
	int graphId = workflowP.getGraphId(workflowId);
	DirectedGraph dg = this.getGraphByGraphId(graphId);

	Integer key = workflowId;
	if (!activeWorkflows.contains(key)) {
	    throw new ExceptieWF("Workflow ID is not active or valid");
	}
	workflowP.setVariable(workflowId, name, value);

	try {
	    String workflowName = dg.getName();
	    int version = dg.getVersion();
	    eventsPublisher.publishVariableUpdatedEvent(workflowName, version,
		    workflowId, name, value);
	} catch (ExceptieWF e) {
	    log.warn("Failed to publish event");
	}
    }

    @SuppressWarnings("unchecked")
    private Integer startContaineeWorkflow(final String workflowName,
	    final int version, final ItemModel _witem, final String initiator,
	    final Integer parentWorkflowId) throws ExceptieWF, SQLException,
	    JaxenException, IOException, ParserConfigurationException,
	    SAXException {

	Integer workflowId = null;
	DirectedGraph dg = this.getGraphByNameAndVersion(workflowName, version);
	ItemModel clonedWItem = _witem.makeCopy();
	log.info("Saving cloned workitem: " + clonedWItem);
	Persistence.getWorkItemP().saveDB(clonedWItem);
	log.info("Cloned workitem: " + clonedWItem);

	int graphId = dg.getGraphId();
	Nod startNode = dg.getRootNode();

	workflowId = workflowP.saveNewWorkflow(graphId, workflowName,
		initiator, parentWorkflowId.intValue());
	log.info("Started containee workflow. Workflow Id is: " + workflowId);

	activeWorkflows.add(workflowId);
	clonedWItem.setWorkflowId(workflowId);

	log.info("startNode is :" + startNode);
	this.transitionFromStartNode(graphId, workflowName, version, startNode,
		clonedWItem);

	try {
	    int thisVersion = -1;
	    if (version == -1) {
		thisVersion = directedGraphP
			.getLatestVersionNumber(workflowName);
	    }
	    eventsPublisher.publishWorkflowStartedEvent(workflowName,
		    thisVersion, new Integer(graphId), parentWorkflowId,
		    initiator, clonedWItem);
	} catch (ExceptieWF e) {
	    log.warn("Failed to publish event");
	}

	return workflowId;
    }

    @SuppressWarnings("unchecked")
    public Integer startWorkflow(final String workflowName, final int version,
	    final ItemModel witem, final String initiator) throws ExceptieWF,
	    SQLException, JaxenException, IOException,
	    ParserConfigurationException, SAXException {
	Integer workflowId = null;
	DirectedGraph dg = this.getGraphByNameAndVersion(workflowName, version);
	log.info("Saving workitem: " + witem);
	Persistence.getWorkItemP().saveDB(witem);

	int graphId = dg.getGraphId();
	Nod startNode = dg.getRootNode();

	log.info("GraphId: " + graphId);

	workflowId = Persistence.getWorkflowP().saveNewWorkflow(graphId,
		workflowName, initiator, -1);

	activeWorkflows.add(workflowId);
	witem.setWorkflowId(workflowId);

	log.info("startNode is :" + startNode);
	this.transitionFromStartNode(graphId, workflowName, version, startNode,
		witem);

	try {
	    int thisVersion = -1;
	    if (version == -1) {
		thisVersion = directedGraphP
			.getLatestVersionNumber(workflowName);
	    }
	    eventsPublisher.publishWorkflowStartedEvent(workflowName,
		    thisVersion, workflowId, new Integer(-1), initiator, witem);
	} catch (ExceptieWF e) {
	    log.warn("Failed to publish event");
	}

	return workflowId;
    }

    @SuppressWarnings("unchecked")
    public void suspendWorkflow(final Integer wfId) throws ExceptieWF,
	    SQLException {
	int graphId = workflowP.getGraphId(wfId);
	DirectedGraph dg = this.getGraphByGraphId(graphId);

	Integer key = wfId;
	if (suspendedWorkflows.contains(key)) {
	    throw new ExceptieWF("Workflow is already suspended");
	}
	suspendedWorkflows.add(key);
	workflowP.suspendWorkflow(wfId);

	try {
	    String workflowName = dg.getName();
	    int version = dg.getVersion();
	    eventsPublisher.publishWorkflowSuspendedEvent(workflowName,
		    version, wfId, "system");
	} catch (ExceptieWF e) {
	    log.warn("Failed to publish event");
	}
    }

    @SuppressWarnings("unchecked")
    private void transitionFrom(final int gid, final String workflowName,
	    final int workflowVersion, final Nod fromNode, final ItemModel witem)
	    throws ExceptieWF, SQLException, JaxenException, IOException,
	    ParserConfigurationException, SAXException {

	List destv = fromNode.getDestinations();
	String processName = fromNode.getName();
	Integer workflowId = witem.getWorkflowId();

	log.info("Transitioning from: " + fromNode.getNodeId() + " "
		+ fromNode.getName());
	log.info("From node has: " + destv.size() + " destinations");
	for (int i = 0; i < destv.size(); i++) {
	    wf.model.Destinatie dest = (wf.model.Destinatie) destv.get(i);

	    log.debug("Processing destination " + i);
	    if (!this.evaluateRule(witem, dest.rule)) {
		log
			.info("This destination's rule evaluated to false. Not going there");
		continue;
	    }

	    Nod node = dest.node;
	    String nodeType = node.getNodeType();

	    log.debug("This destination node is: " + node.getNodeId() + " "
		    + node.getName());
	    log.debug("This destination node type is: " + node.getNodeType());
	    if (nodeType.equals(Nod.END)) {

		log.info("Processing END node");
		inboxP.removeWorkItem(gid, processName, witem);
		PopNode popNode = processStack.pop(workflowId.intValue(), node);
		if (popNode == null) {
		    int thisVersion = -1;
		    if (workflowVersion == -1) {
			thisVersion = directedGraphP
				.getLatestVersionNumber(workflowName);
		    }

		    Integer key = workflowId;
		    activeWorkflows.remove(key);
		    workflowP.setCompleted(workflowId);

		    try {
			eventsPublisher
				.publishWorkflowCompletedEvent(workflowName,
					thisVersion, workflowId, "system");
		    } catch (ExceptieWF e) {
			log.warn("Failed to publish event");
		    }
		} else {
		    log.info("Transitioning to unwoundNode's destinations.");
		    int cGid = popNode.gid;
		    int cNodeId = popNode.nodeId;
		    log.info("cGid = " + cGid + " cNodeId = " + cNodeId);
		    DirectedGraph dg = this.getGraphByGraphId(cGid);
		    log.info("Got graph");
		    Nod cNode = dg.getNode(cNodeId);
		    log.info("cNode = " + cNode);
		    String cWorkflowName = dg.getName();
		    int cVersion = dg.getVersion();
		    log.info("cWorkflowName = " + cWorkflowName + " Version = "
			    + cVersion);
		    this.transitionFrom(cGid, cWorkflowName, cVersion, cNode,
			    witem);
		}
		continue;
	    }

	    String nextProcessName = node.getName();
	    String nextProcessType = node.getNodeType();
	    if (nextProcessType.equals(Nod.OR)) {

		log.info("Processing OR node");
		if (this.orTransitionHasOccurred(workflowId, node.getNodeId())) {
		    continue;
		}
		this.recordOrTransition(workflowId, node.getNodeId());

		List orDest = node.getDestinations();
		Nod orDestNode = ((wf.model.Destinatie) orDest.get(0)).node;
		nextProcessName = orDestNode.getName();
		nextProcessType = orDestNode.getNodeType();
		if (nextProcessType.equals(Nod.PROCESS)) {
		    log.info("Transitioning to: " + nextProcessName);
		    this.transitionTo(gid, workflowName, workflowId,
			    workflowVersion, processName, nextProcessName,
			    witem);
		} else {
		    throw new ExceptieWF("Next node is not process!!");
		}
	    } else if (nextProcessType.equals(Nod.AND)) {
		log.info("Processing AND node");
		int destNodeId = node.getNodeId();
		int fromNodeId = fromNode.getNodeId();
		waitingP.addProcess(workflowId, destNodeId, fromNodeId);
		inboxP.removeWorkItem(gid, processName, witem);

		List fromNodes = node.getFromNodes();
		if (waitingP.allProcessesArrived(fromNodes, workflowId,
			destNodeId)) {
		    log.info("Waiting on AND node completed.");
		    waitingP.removeProcesses(workflowId);
		    this.transitionFrom(gid, workflowName, workflowVersion,
			    node, witem);
		}
	    } else if (nextProcessType.equals(Nod.PROCESS)) {
		log.info("Transitioning to: " + nextProcessName);
		this.transitionTo(gid, workflowName, workflowId,
			workflowVersion, processName, nextProcessName, witem);
	    } else if (nextProcessType.equals(Nod.CONTAINER)) {
		log.info("Processing CONTAINER node");
		this.processContainer(gid, node, witem);
	    }
	}
    }

    @SuppressWarnings("unchecked")
    private void transitionFromStartNode(final int gid,
	    final String workflowName, final int workflowVersion,
	    final Nod startNode, final ItemModel witem) throws ExceptieWF,
	    JaxenException, IOException, ParserConfigurationException,
	    SAXException {
	List destv = startNode.getDestinations();
	for (int i = 0; i < destv.size(); i++) {
	    wf.model.Destinatie dest = (wf.model.Destinatie) destv.get(i);
	    if (!this.evaluateRule(witem, dest.rule)) {
		continue;
	    }

	    Nod node = dest.node;
	    String nodeType = node.getNodeType();
	    if (nodeType.equals(Nod.END)) {
		continue;
	    }
	    String procName = node.getName();
	    log.info("Adding workitem to inbox for proc: " + procName);
	    inboxP.addWorkItem(gid, workflowName, procName, witem);
	    log.info("Transition From Start Nod");
	    log.info("Sending inbox notification:");
	    log.info("   workflowName: " + workflowName);
	    log.info("   procName: " + procName);
	    log.info("   witem: " + witem.getId());
	    this.sendInboxNotification(workflowName, procName, witem);
	}
    }

    private void transitionTo(final int gid, final String workflowName,
	    final Integer workflowId, final int workflowVersion,
	    final String processName, final String nextProcessName,
	    final ItemModel witem) throws ExceptieWF {

	inboxP.removeWorkItem(gid, processName, witem);
	inboxP.addWorkItem(gid, workflowName, nextProcessName, witem);

	log.info("TransitionTo");
	log.info("Sending inbox notification:");
	log.info("   workflowName: " + workflowName);
	log.info("   procName: " + nextProcessName);
	log.info("   witem: " + witem.getId());
	this.sendInboxNotification(workflowName, nextProcessName, witem);

	try {
	    int thisVersion = -1;
	    if (workflowVersion == -1) {
		thisVersion = directedGraphP
			.getLatestVersionNumber(workflowName);
	    }
	    eventsPublisher.publishNodeTransitionEvent(workflowName,
		    thisVersion, workflowId, processName, nextProcessName,
		    witem);
	} catch (Exception e) {
	    log.warn("Failed to publish event", e);
	}

    }

    public boolean validateProcess(final String workflowName,
	    final int workflowVersion, final String processName)
	    throws ExceptieWF, SQLException {

	boolean result = false;
	DirectedGraph dg = this.getGraphByNameAndVersion(workflowName,
		workflowVersion);
	Nod node = dg.getNode(processName);
	if (node != null) {
	    result = true;
	}

	return result;
    }
}
