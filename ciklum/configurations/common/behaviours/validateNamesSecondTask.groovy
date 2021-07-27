package ciklum.configurations.common.behaviours

import java.net.URLConnection
import groovy.json.JsonSlurper
import org.apache.groovy.json.internal.LazyMap
import com.onresolve.jira.groovy.user.FormField
import com.onresolve.jira.groovy.user.FieldBehaviours
import groovy.transform.BaseScript

@BaseScript FieldBehaviours fieldBehaviours

/*
    better use field id's, but for working on any jira instance
*/

String FRED_FIELD_NAME = 'Fred'
String DAN_FIELD_NAME = 'Dan'
String DOMINIC_FIELD_NAME = 'Dominic'
String ALEX_FIELD_NAME = 'Alex'
String BOB_FIELD_NAME = 'Bob'
String KEVIN_FIELD_NAME = 'Kevin'
String WILLIAM_FIELD_NAME = 'William'
String JOHN_FIELD_NAME = 'John'

FormField fredField = getFieldByName(FRED_FIELD_NAME) 
FormField danField = getFieldByName(DAN_FIELD_NAME)
FormField dominicField = getFieldByName(DOMINIC_FIELD_NAME)
FormField alexField = getFieldByName(ALEX_FIELD_NAME)
FormField bobField = getFieldByName(BOB_FIELD_NAME)
FormField kevinField = getFieldByName(KEVIN_FIELD_NAME)
FormField williamField = getFieldByName(WILLIAM_FIELD_NAME)
FormField johnField = getFieldByName(JOHN_FIELD_NAME)

Map<String, FormField> fields = [
    				 (FRED_FIELD_NAME) : (fredField),
                                 (DAN_FIELD_NAME) : (danField),
                          	 (DOMINIC_FIELD_NAME) : (dominicField),
                          	 (ALEX_FIELD_NAME) : (alexField),
                          	 (BOB_FIELD_NAME) : (bobField),
                          	 (KEVIN_FIELD_NAME) : (kevinField),
                          	 (WILLIAM_FIELD_NAME) : (williamField),
                          	 (JOHN_FIELD_NAME) : (johnField)]

LazyMap getDataFromRest(String parameter) {
    final String URL = "https://api.agify.io/?name=${parameter}"
    JsonSlurper jsonFormatter = new JsonSlurper()
    URLConnection connection = URL.toURL().openConnection()
    jsonFormatter.parseText(connection.content.text) as LazyMap
}

Character lastCharacter = null
List arr = []

fields.each { Object<String, FormField> field ->
    LazyMap fieldInfo = getDataFromRest(field.key)
    
    Integer age = fieldInfo.getAt('age') as Integer
    arr.add(age)
    /*
    	здесь задание выглядит как-то нелогично, все условия в конечном итоге будут правильными
        сделал как нахождение атрибута "age" в промежутке между числами, чтобы каждое условие было разное
    */
    age >= 0 & age <= 15 ? field.value.setReadOnly(true) : field.value.setReadOnly(false) //1st condition
    age > 15 & age <= 25 ? field.value.setRequired(true) : field.value.setRequired(false) //2nd condition
    age >= 26 & age <= 30 ? field.value.setHidden(true) : field.value.setHidden(false) //3d condition
    
    if (age >= 31 & age >= 35) {
        if (!lastCharacter) { //если до этого уже не было поля с атрибутом "age >= 31 <= 35"
        	lastCharacter = (field.key.chars as Set).last()
            fields.each { Object<String, FormField> otherField ->
                if (lastCharacter.toString().toUpperCase()) {
                    otherField.value.setFormValue(field.key)
                }
            }
        }
    } 
}
