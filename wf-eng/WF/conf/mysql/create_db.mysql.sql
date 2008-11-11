/*==========================================================================*/
/* Project Filename:    C:\temp\xflow.dez                                   */
/* Project Name:                                                            */
/* Author:                                                                  */
/* DBMS:                Mysql                                       */
/* Copyright:                                                               */
/* Generated on:        7/19/2004 10:42:12 PM                               */
/*==========================================================================*/

/*==========================================================================*/
/*  Tables                                                                  */
/*==========================================================================*/

CREATE TABLE destination (
  rule TEXT,
  nid INT4,
  destnid INT4
);

CREATE TABLE graph (
  gid INT4 NOT NULL,
  name VARCHAR(64),
  description TEXT,
  version INT4,
  nid INT4,
  PRIMARY KEY (gid)
);

CREATE TABLE node (
  nid INT4 NOT NULL AUTO_INCREMENT,
  gid INT4,
  name VARCHAR(64),
  nodetype VARCHAR(32),
  description TEXT,
  PRIMARY KEY (nid)
);

CREATE TABLE nodeprops (
  nid INT4 NOT NULL,
  name VARCHAR(64) NOT NULL,
  value TEXT,
  PRIMARY KEY (nid, name)
);

CREATE TABLE workflow (
  workflowId INT4 NOT NULL AUTO_INCREMENT,
  initiator VARCHAR(64),
  isActive BOOL,
  timeStarted DATE,
  timeEnded DATE,
  status VARCHAR(32),
  parentworkflowId INT4,
  gid INT4,
  PRIMARY KEY (workflowId)
);

CREATE TABLE inbox (
  workflowId INT4,
  workitemId INT4,
  gid INT4,
  workflowName VARCHAR(64),
  procName VARCHAR(64),
  timeStarted DATE,
  timeout BOOL
);

CREATE TABLE workitem (
  workitemId INT4 NOT NULL AUTO_INCREMENT,
  payload TEXT,
  payloadType VARCHAR(16),
  PRIMARY KEY (workitemId)
);

CREATE TABLE workitemprops (
  workitemId INT4 NOT NULL,
  workflowName VARCHAR(64) NOT NULL,
  procName VARCHAR(64) NOT NULL,
  name VARCHAR(64),
  value TEXT,
  PRIMARY KEY (workitemId, workflowName, procName)
);

CREATE TABLE workflowvars (
  workflowId INT4 NOT NULL,
  name VARCHAR(64) NOT NULL,
  value TEXT,
  PRIMARY KEY (workflowId, name)
);

CREATE TABLE waiting (
  workflowId INT4 NOT NULL,
  fromNodeId INT4 NOT NULL,
  destNodeId INT4 NOT NULL,
  PRIMARY KEY (workflowId, fromNodeId, destNodeId)
);

CREATE TABLE procstack (
  workflowId INT4,
  cgid INT4,
  cNodeId INT4,
  endNodeId INT4
);

CREATE TABLE ortab (
  workflowId INT4,
  nodeid INT4
);

CREATE TABLE evt_event (
  eventId INT NOT NULL AUTO_INCREMENT,
  eventType VARCHAR(32) NOT NULL,
  timestamp DATE NOT NULL,
  workflowName VARCHAR(64) NOT NULL,
  workflowVersion INT4 NOT NULL,
  workflowInstanceId INT4,
  parentWorkflowInstanceId INT4,
  user_login VARCHAR(64),
  primary key ( eventId )
);

CREATE TABLE evt_NodeTransitionEvent (
  eventId INT4 NOT NULL,
  fromNodeName VARCHAR(64) NOT NULL,
  fromNodeType VARCHAR(16) NOT NULL,
  toNodeName VARCHAR(64) NOT NULL,
  toNodeType VARCHAR(16) NOT NULL
);

CREATE TABLE evt_ProcessTimedOutEvent (
  eventId INT4 NOT NULL,
  processName VARCHAR(64) NOT NULL
);

CREATE TABLE evt_VariableUpdateEvent (
  eventId INT4 NOT NULL,
  variableName VARCHAR(64) NOT NULL,
  variableType VARCHAR(16) NOT NULL,
  variableValue TEXT NOT NULL
);

CREATE TABLE evt_EventWorkItem (
  eventId INT4 NOT NULL,
  workItemInternalId INTEGER AUTO_INCREMENT,
  workItemId INT4 NOT NULL,
  payloadType VARCHAR(16),
  payload TEXT,
  primary key (workItemInternalId)
);

CREATE TABLE evt_EventWorkItemProperties (
  workItemInternalId INT4 NOT NULL,
  propertyName VARCHAR(64) NOT NULL,
  propertyType VARCHAR(16) NOT NULL,
  propertyValue TEXT NOT NULL
);


CREATE TABLE dbcp_helper (
  val integer
);

insert into dbcp_helper( val ) VALUES ( 5 );
insert into dbcp_helper( val ) VALUES ( 5 );

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
