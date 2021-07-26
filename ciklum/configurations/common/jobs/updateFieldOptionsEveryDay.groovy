/**
    Purpose: load options from rest and update field every day
    Usage: all projects, field configuration
    Preconditions: None
    Enviroment: JIRA Software 8.15.0, ScriptRunner v. 6.30.2
    Script type: Filesystem
    Author: Gleb Yudenok
*/

package ciklum.configurations.common.jobs

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.CustomFieldManager
import com.atlassian.jira.issue.customfields.manager.OptionsManager
import com.atlassian.jira.issue.IssueManager
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.bc.issue.IssueService
import com.atlassian.jira.issue.fields.CustomField
import com.atlassian.jira.issue.fields.config.FieldConfig
import com.atlassian.jira.issue.customfields.option.Options
import com.atlassian.jira.user.ApplicationUser
import java.net.URLConnection
import groovy.json.JsonSlurper
import groovy.transform.Field
import org.apache.groovy.json.internal.LazyMap
import ciklum.configurations.common.custom_fields.Fields

@Field CustomFieldManager cfManager = ComponentAccessor.customFieldManager
@Field OptionsManager optionsManager = ComponentAccessor.optionsManager
@Field IssueManager issueManager = ComponentAccessor.issueManager
@Field IssueService issueService = ComponentAccessor.issueService

/*
    substitute key of any your entity
*/
final String entityId = 'MYT-2'
@Field Issue issue = issueManager.getIssueObject(entityId)
@Field CustomField namesField = cfManager.getCustomFieldObject(Fields.NAMES)

static getDataFromRest(String url) {
    JsonSlurper jsonFormatter = new JsonSlurper()
    URLConnection connection = url.toURL().openConnection()
    jsonFormatter.parseText(connection.content.text)
}

final String URL = 'https://6069fa18e1c2a10017545286.mockapi.io/api/v1/names'
ArrayList output = getDataFromRest(URL) 

FieldConfig fieldConfig = namesField.getRelevantConfig(issue)
Options currentOptions = optionsManager.getOptions(fieldConfig)
deleteOptions()

List options = new ArrayList()

output.each {
    def newSeqId
    if (currentOptions) {
        newSeqId = currentOptions*.sequence.max() - 1
    } else { 
        newSeqId = 0L
    }   
    options.add(optionsManager.createOption(fieldConfig, null, newSeqId, it.getAt('name')))
}

ApplicationUser currentUser = ComponentAccessor.jiraAuthenticationContext.loggedInUser
def issueInputParameters = issueService.newIssueInputParameters()

options.each { option ->
    issueInputParameters.with {
	addCustomFieldValue(option.value, null)
    }
}

def updateValidationResult = issueService.validateUpdate(currentUser, issue.id, issueInputParameters)
if (updateValidationResult.isValid()) {
    issueService.update(currentUser, updateValidationResult)
}
else {
    log.warn("Failed to update issue: ${issue.key}: ${updateValidationResult.errorCollection}")
}

public void deleteOptions() {
    Options options = optionsManager.getOptions(namesField.getRelevantConfig(issue))
    options.each {
        optionsManager.deleteOptionAndChildren(it)
    }
}
