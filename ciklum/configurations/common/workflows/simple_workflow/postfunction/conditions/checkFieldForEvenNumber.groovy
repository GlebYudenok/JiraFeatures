/**
	3 task
	Purpose: transition to "In progress" if field "Names" contains even number
	Usage: simple workflow, create postfunction, fast-track transition
	Preconditions: None
	Enviroment: JIRA Software 8.15.0, ScriptRunner v. 6.30.2
	Script type: Filesystem
	Author: Gleb Yudenok
*/
package ciklum.configurations.common.workflows.simple_workflow.postfunction.conditions

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.CustomFieldManager
import com.atlassian.jira.issue.fields.CustomField
import ciklum.configurations.common.custom_fields.Fields

CustomFieldManager cfManager = ComponentAccessor.customFieldManager
CustomField namesField = cfManager.getCustomFieldObject(Fields.NAMES)
String namesValue = issue.getCustomFieldValue(namesField)
Integer numberPartOfField = namesValue.split(' ')[1].toInteger()

return numberPartOfField % 2 == 0 //even number