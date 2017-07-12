package com.howell.bean.httpbean;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/4/12.
 */

public class EventRecord {
    String id;
    String componentId;
    String name;
    String eventType;
    String alarmTime;
    int severity;
    String disalarmTime;
    String processTime;
    String processDescription;
    String description;
    String objectType;
    double triggerValue;
    ArrayList<String> pictureId;
    ArrayList<EventRecordedFile> eventRecordedFiles;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getComponentId() {
        return componentId;
    }

    public void setComponentId(String componentId) {
        this.componentId = componentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getAlarmTime() {
        return alarmTime;
    }

    public void setAlarmTime(String alarmTime) {
        this.alarmTime = alarmTime;
    }

    public int getSeverity() {
        return severity;
    }

    public void setSeverity(int severity) {
        this.severity = severity;
    }

    public String getDisalarmTime() {
        return disalarmTime;
    }

    public void setDisalarmTime(String disalarmTime) {
        this.disalarmTime = disalarmTime;
    }

    public String getProcessTime() {
        return processTime;
    }

    public void setProcessTime(String processTime) {
        this.processTime = processTime;
    }

    public String getProcessDescription() {
        return processDescription;
    }

    public void setProcessDescription(String processDescription) {
        this.processDescription = processDescription;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public double getTriggerValue() {
        return triggerValue;
    }

    public void setTriggerValue(double triggerValue) {
        this.triggerValue = triggerValue;
    }

    public ArrayList<String> getPictureId() {
        return pictureId;
    }

    public void setPictureId(ArrayList<String> pictureId) {
        this.pictureId = pictureId;
    }

    public ArrayList<EventRecordedFile> getEventRecordedFiles() {
        return eventRecordedFiles;
    }

    public void setEventRecordedFiles(ArrayList<EventRecordedFile> eventRecordedFiles) {
        this.eventRecordedFiles = eventRecordedFiles;
    }

    public EventRecord(String id, String componentId, String name, String eventType, String alarmTime, int severity, String disalarmTime, String processTime, String processDescription, String description, String objectType, double triggerValue, ArrayList<String> pictureId, ArrayList<EventRecordedFile> eventRecordedFiles) {
        this.id = id;
        this.componentId = componentId;
        this.name = name;
        this.eventType = eventType;
        this.alarmTime = alarmTime;
        this.severity = severity;
        this.disalarmTime = disalarmTime;
        this.processTime = processTime;
        this.processDescription = processDescription;
        this.description = description;
        this.objectType = objectType;
        this.triggerValue = triggerValue;
        this.pictureId = pictureId;
        this.eventRecordedFiles = eventRecordedFiles;
    }

    public EventRecord() {
    }

    @Override
    public String toString() {
        return "EventRecord{" +
                "id='" + id + '\'' +
                ", componentId='" + componentId + '\'' +
                ", name='" + name + '\'' +
                ", eventType='" + eventType + '\'' +
                ", alarmTime='" + alarmTime + '\'' +
                ", severity=" + severity +
                ", disalarmTime='" + disalarmTime + '\'' +
                ", processTime='" + processTime + '\'' +
                ", processDescription='" + processDescription + '\'' +
                ", description='" + description + '\'' +
                ", objectType='" + objectType + '\'' +
                ", triggerValue=" + triggerValue +
                ", pictureId=" + pictureId +
                ", eventRecordedFiles=" + eventRecordedFiles +
                '}';
    }
}
