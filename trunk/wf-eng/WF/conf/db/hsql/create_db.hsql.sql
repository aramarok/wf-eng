/*==========================================================================*/
/* Project Filename:    C:\temp\wfprj.dez                                   */
/* Project Name:                                                            */
/* Author:                                                                  */
/* DBMS:                hsql                                        */
/* Copyright:                                                               */
/* Generated on:        7/19/2004 10:42:12 PM                               */
/*==========================================================================*/

/*==========================================================================*/
/*  Tables                                                                  */
/*==========================================================================*/

CREATE TABLE destination (
  rule VARCHAR(300),
  nid integer,
  destnid integer
);

CREATE TABLE graph (
  gid integer NOT NULL,
  name VARCHAR(64),
  description VARCHAR(500),
  version integer,
  nid integer,
  PRIMARY KEY (gid)
);

CREATE TABLE node (
  nid IDENTITY NOT NULL,
  gid integer,
  name VARCHAR(64),
  nodetype VARCHAR(32),
  description VARCHAR(500)
);

CREATE TABLE nodeprops (
  nid integer NOT NULL,
  name VARCHAR(64) NOT NULL,
  value VARCHAR(5000),
  PRIMARY KEY (nid, name)
);

CREATE TABLE workflow (
  workflowId IDENTITY NOT NULL,
  initiator VARCHAR(64),
  isActive bit,
  timeStarted DATE,
  timeEnded DATE,
  status VARCHAR(32),
  parentworkflowId integer,
  gid integer
);

CREATE TABLE inbox (
  workflowId integer,
  workitemId integer,
  gid integer,
  workflowName VARCHAR(64),
  procName VARCHAR(64),
  timeStarted DATE,
  timeout bit
);

CREATE TABLE workitem (
  workitemId IDENTITY NOT NULL,
  payload VARCHAR(5000),
  payloadType VARCHAR(16)
);

CREATE TABLE workitemprops (
  workitemId integer NOT NULL,
  workflowName VARCHAR(64) NOT NULL,
  procName VARCHAR(64) NOT NULL,
  name VARCHAR(64),
  value VARCHAR(5000),
  PRIMARY KEY (workitemId, workflowName, procName)
);

CREATE TABLE workflowvars (
  workflowId integer NOT NULL,
  name VARCHAR(64) NOT NULL,
  value VARCHAR(5000),
  PRIMARY KEY (workflowId, name)
);

CREATE TABLE waiting (
  workflowId integer NOT NULL,
  fromNodeId integer NOT NULL,
  destNodeId integer NOT NULL,
  PRIMARY KEY (workflowId, fromNodeId, destNodeId)
);

CREATE TABLE procstack (
  workflowId integer,
  cgid integer,
  cNodeId integer,
  endNodeId integer
);

CREATE TABLE ortab (
  workflowId integer,
  nodeid integer
);

CREATE TABLE evt_event (
  eventId IDENTITY,
  eventType VARCHAR(32) NOT NULL,
  timestamp DATE NOT NULL,
  workflowName VARCHAR(64) NOT NULL,
  workflowVersion integer NOT NULL,
  workflowInstanceId integer,
  parentWorkflowInstanceId integer,
  user_login VARCHAR(64)
);

CREATE TABLE evt_NodeTransitionEvent (
  eventId integer NOT NULL,
  fromNodeName VARCHAR(64) NOT NULL,
  fromNodeType VARCHAR(16) NOT NULL,
  toNodeName VARCHAR(64) NOT NULL,
  toNodeType VARCHAR(16) NOT NULL
);

CREATE TABLE evt_ProcessTimedOutEvent (
  eventId integer NOT NULL,
  processName VARCHAR(64) NOT NULL
);

CREATE TABLE evt_VariableUpdateEvent (
  eventId integer NOT NULL,
  variableName VARCHAR(64) NOT NULL,
  variableType VARCHAR(16) NOT NULL,
  variableValue VARCHAR(5000) NOT NULL
);

CREATE TABLE evt_EventWorkItem (
  eventId integer NOT NULL,
  workItemInternalId IDENTITY,
  workItemId integer NOT NULL,
  payloadType VARCHAR(16),
  payload VARCHAR(5000)
);

CREATE TABLE evt_EventWorkItemProperties (
  workItemInternalId integer NOT NULL,
  propertyName VARCHAR(64) NOT NULL,
  propertyType VARCHAR(16) NOT NULL,
  propertyValue VARCHAR(5000) NOT NULL
);

CREATE TABLE DBCP_HELPER (
  val integer
);

insert into DBCP_HELPER( val ) VALUES ( 5 );

/*==========================================================================*/
/*  Foreign Keys                                                            */
/*==========================================================================*/

ALTER TABLE destination
  ADD CONSTRAINT node_dest_2 FOREIGN KEY (nid) REFERENCES node (nid);

ALTER TABLE destination
  ADD CONSTRAINT node_dest_1 FOREIGN KEY (destnid) REFERENCES node (nid);

ALTER TABLE graph
  ADD FOREIGN KEY (nid) REFERENCES node (nid);

ALTER TABLE nodeprops
  ADD FOREIGN KEY (nid) REFERENCES node (nid);

ALTER TABLE workflow
  ADD FOREIGN KEY (parentworkflowId) REFERENCES workflow (workflowId);

ALTER TABLE workflow
  ADD FOREIGN KEY (gid) REFERENCES graph (gid);

ALTER TABLE inbox
  ADD FOREIGN KEY (workflowId) REFERENCES workflow (workflowId);

ALTER TABLE inbox
  ADD FOREIGN KEY (workitemId) REFERENCES workitem (workitemId);

ALTER TABLE workitemprops
  ADD FOREIGN KEY (workitemId) REFERENCES workitem (workitemId);

ALTER TABLE workflowvars
  ADD FOREIGN KEY (workflowId) REFERENCES workflow (workflowId);

ALTER TABLE waiting
  ADD FOREIGN KEY (workflowId) REFERENCES workflow (workflowId);

ALTER TABLE waiting
  ADD CONSTRAINT node_waiting_2 FOREIGN KEY (fromNodeId) REFERENCES node (nid);

ALTER TABLE waiting
  ADD CONSTRAINT node_waiting_1 FOREIGN KEY (destNodeId) REFERENCES node (nid);

ALTER TABLE procstack
  ADD FOREIGN KEY (workflowId) REFERENCES workflow (workflowId);

ALTER TABLE ortab
  ADD FOREIGN KEY (workflowId) REFERENCES workflow (workflowId);

ALTER TABLE ortab
  ADD FOREIGN KEY (nodeid) REFERENCES node (nid);

/*==========================================================================*/
/*  Indexes                                                                 */
/*==========================================================================*/

/*==========================================================================*/
/*  Sequences                                                               */
/*==========================================================================*/


/*==========================================================================*/
/*  Views                                                                   */
/*==========================================================================*/

/*==========================================================================*/
/*  Procedures                                                              */
/*==========================================================================*/

/*==========================================================================*/
/*  Triggers                                                                */
/*==========================================================================*/
