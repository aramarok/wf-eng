/*==========================================================================*/
/* DROP SCRIPT                                                              */
/*==========================================================================*/
/* Project Filename:    C:\temp\wfprj.dez                                   */
/* Project Name:                                                            */
/* Author:                                                                  */
/* DBMS:                PostgreSQL 7                                        */
/* Copyright:                                                               */
/* Generated on:        7/19/2004 10:31:40 PM                               */
/*==========================================================================*/

/*==========================================================================*/
/*  Drop Indexes                                                            */
/*==========================================================================*/

/*==========================================================================*/
/*  Drop Column Constraints                                                 */
/*==========================================================================*/

/*==========================================================================*/
/*  Drop Table Constraints                                                  */
/*==========================================================================*/

/*==========================================================================*/
/*  Drop Triggers                                                           */
/*==========================================================================*/

/*==========================================================================*/
/*  Drop Sequences                                                          */
/*==========================================================================*/
DROP SEQUENCE workitem_seq CASCADE;
DROP SEQUENCE node_seq CASCADE;
DROP SEQUENCE workflow_seq CASCADE;

/*==========================================================================*/
/*  Drop Procedures                                                         */
/*==========================================================================*/

/*==========================================================================*/
/*  Drop Views                                                              */
/*==========================================================================*/

/*==========================================================================*/
/*  Drop Tables                                                             */
/*==========================================================================*/
DROP TABLE evt_EventWorkItemProperties CASCADE;
DROP TABLE evt_EventWorkItem CASCADE;
DROP TABLE evt_VariableUpdateEvent CASCADE;
DROP TABLE evt_ProcessTimedOutEvent CASCADE;
DROP TABLE evt_NodeTransitionEvent CASCADE;
DROP TABLE evt_event CASCADE;
DROP TABLE ortab CASCADE;
DROP TABLE procstack CASCADE;
DROP TABLE waiting CASCADE;
DROP TABLE workflowvars CASCADE;
DROP TABLE workitemprops CASCADE;
DROP TABLE workitem CASCADE;
DROP TABLE inbox CASCADE;
DROP TABLE workflow CASCADE;
DROP TABLE nodeprops CASCADE;
DROP TABLE node CASCADE;
DROP TABLE graph CASCADE;
DROP TABLE destination CASCADE;
DROP TABLE DBCP_HELPER;