//package com.github.darylyeung.zentaoplugin.extension.zentao.model;
//
//import com.google.gson.annotations.SerializedName;
//
//import java.util.List;
//
///**
// * @author Yeung
// * @version v1.0
// * @date 2024-01-11 22:18:50
// */
//public class ZentaoBug {
//    private int id;
//    private int project;
//    private int product;
//    private int injection;
//    private int identify;
//    private int branch;
//    private int module;
//    private int execution;
//    private int plan;
//    private int story;
//    private int storyVersion;
//    private int task;
//    private int toTask;
//    private int toStory;
//    private String title;
//    private String keywords;
//    private int severity;
//    private int pri;
//    private String type;
//    private String os;
//    private String browser;
//    private String hardware;
//    private String found;
//    private String steps;
//    private String status;
//    private String subStatus;
//    private String color;
//    private int confirmed;
//    private int activatedCount;
//    private String activatedDate;
//    private String feedbackBy;
//    private String notifyEmail;
//    private List<Object> mailto;
//    private ZentaoUserInfo openedBy;
//    private String openedDate;
//    private String openedBuild;
//    private ZentaoUserInfo assignedTo;
//    private String assignedDate;
//    private String deadline;
//    private ZentaoUserInfo resolvedBy;
//    private String resolution;
//    private String resolvedBuild;
//    private String resolvedDate;
//    private ZentaoUserInfo closedBy;
//    private String closedDate;
//    private int duplicateBug;
//    private String linkBug;
//    @SerializedName("case")
//    private int case1;
//    private int caseVersion;
//    private int feedback;
//    private int result;
//    private int repo;
//    private int mr;
//    private String entry;
//    private String lines;
//    private String v1;
//    private String v2;
//    private String repoType;
//    private String issueKey;
//    private int testtask;
//    private ZentaoUserInfo lastEditedBy;
//    private String lastEditedDate;
//    private boolean deleted;
//    private String priOrder;
//    private int severityOrder;
//    private int delay;
//    private boolean needconfirm;
//    private String statusName;
//    private String productStatus;
//
//    public int getId() {
//        return id;
//    }
//
//    public void setId(int id) {
//        this.id = id;
//    }
//
//    public int getProject() {
//        return project;
//    }
//
//    public void setProject(int project) {
//        this.project = project;
//    }
//
//    public int getProduct() {
//        return product;
//    }
//
//    public void setProduct(int product) {
//        this.product = product;
//    }
//
//    public int getInjection() {
//        return injection;
//    }
//
//    public void setInjection(int injection) {
//        this.injection = injection;
//    }
//
//    public int getIdentify() {
//        return identify;
//    }
//
//    public void setIdentify(int identify) {
//        this.identify = identify;
//    }
//
//    public int getBranch() {
//        return branch;
//    }
//
//    public void setBranch(int branch) {
//        this.branch = branch;
//    }
//
//    public int getModule() {
//        return module;
//    }
//
//    public void setModule(int module) {
//        this.module = module;
//    }
//
//    public int getExecution() {
//        return execution;
//    }
//
//    public void setExecution(int execution) {
//        this.execution = execution;
//    }
//
//    public int getPlan() {
//        return plan;
//    }
//
//    public void setPlan(int plan) {
//        this.plan = plan;
//    }
//
//    public int getStory() {
//        return story;
//    }
//
//    public void setStory(int story) {
//        this.story = story;
//    }
//
//    public int getStoryVersion() {
//        return storyVersion;
//    }
//
//    public void setStoryVersion(int storyVersion) {
//        this.storyVersion = storyVersion;
//    }
//
//    public int getTask() {
//        return task;
//    }
//
//    public void setTask(int task) {
//        this.task = task;
//    }
//
//    public int getToTask() {
//        return toTask;
//    }
//
//    public void setToTask(int toTask) {
//        this.toTask = toTask;
//    }
//
//    public int getToStory() {
//        return toStory;
//    }
//
//    public void setToStory(int toStory) {
//        this.toStory = toStory;
//    }
//
//    public String getTitle() {
//        return title;
//    }
//
//    public void setTitle(String title) {
//        this.title = title;
//    }
//
//    public String getKeywords() {
//        return keywords;
//    }
//
//    public void setKeywords(String keywords) {
//        this.keywords = keywords;
//    }
//
//    public int getSeverity() {
//        return severity;
//    }
//
//    public void setSeverity(int severity) {
//        this.severity = severity;
//    }
//
//    public int getPri() {
//        return pri;
//    }
//
//    public void setPri(int pri) {
//        this.pri = pri;
//    }
//
//    public String getType() {
//        return type;
//    }
//
//    public void setType(String type) {
//        this.type = type;
//    }
//
//    public String getOs() {
//        return os;
//    }
//
//    public void setOs(String os) {
//        this.os = os;
//    }
//
//    public String getBrowser() {
//        return browser;
//    }
//
//    public void setBrowser(String browser) {
//        this.browser = browser;
//    }
//
//    public String getHardware() {
//        return hardware;
//    }
//
//    public void setHardware(String hardware) {
//        this.hardware = hardware;
//    }
//
//    public String getFound() {
//        return found;
//    }
//
//    public void setFound(String found) {
//        this.found = found;
//    }
//
//    public String getSteps() {
//        return steps;
//    }
//
//    public void setSteps(String steps) {
//        this.steps = steps;
//    }
//
//    public String getStatus() {
//        return status;
//    }
//
//    public void setStatus(String status) {
//        this.status = status;
//    }
//
//    public String getSubStatus() {
//        return subStatus;
//    }
//
//    public void setSubStatus(String subStatus) {
//        this.subStatus = subStatus;
//    }
//
//    public String getColor() {
//        return color;
//    }
//
//    public void setColor(String color) {
//        this.color = color;
//    }
//
//    public int getConfirmed() {
//        return confirmed;
//    }
//
//    public void setConfirmed(int confirmed) {
//        this.confirmed = confirmed;
//    }
//
//    public int getActivatedCount() {
//        return activatedCount;
//    }
//
//    public void setActivatedCount(int activatedCount) {
//        this.activatedCount = activatedCount;
//    }
//
//    public String getActivatedDate() {
//        return activatedDate;
//    }
//
//    public void setActivatedDate(String activatedDate) {
//        this.activatedDate = activatedDate;
//    }
//
//    public String getFeedbackBy() {
//        return feedbackBy;
//    }
//
//    public void setFeedbackBy(String feedbackBy) {
//        this.feedbackBy = feedbackBy;
//    }
//
//    public String getNotifyEmail() {
//        return notifyEmail;
//    }
//
//    public void setNotifyEmail(String notifyEmail) {
//        this.notifyEmail = notifyEmail;
//    }
//
//    public List<Object> getMailto() {
//        return mailto;
//    }
//
//    public void setMailto(List<Object> mailto) {
//        this.mailto = mailto;
//    }
//
//    public ZentaoUserInfo getOpenedBy() {
//        return openedBy;
//    }
//
//    public void setOpenedBy(ZentaoUserInfo openedBy) {
//        this.openedBy = openedBy;
//    }
//
//    public String getOpenedDate() {
//        return openedDate;
//    }
//
//    public void setOpenedDate(String openedDate) {
//        this.openedDate = openedDate;
//    }
//
//    public String getOpenedBuild() {
//        return openedBuild;
//    }
//
//    public void setOpenedBuild(String openedBuild) {
//        this.openedBuild = openedBuild;
//    }
//
//    public ZentaoUserInfo getAssignedTo() {
//        return assignedTo;
//    }
//
//    public void setAssignedTo(ZentaoUserInfo assignedTo) {
//        this.assignedTo = assignedTo;
//    }
//
//    public String getAssignedDate() {
//        return assignedDate;
//    }
//
//    public void setAssignedDate(String assignedDate) {
//        this.assignedDate = assignedDate;
//    }
//
//    public String getDeadline() {
//        return deadline;
//    }
//
//    public void setDeadline(String deadline) {
//        this.deadline = deadline;
//    }
//
//    public ZentaoUserInfo getResolvedBy() {
//        return resolvedBy;
//    }
//
//    public void setResolvedBy(ZentaoUserInfo resolvedBy) {
//        this.resolvedBy = resolvedBy;
//    }
//
//    public String getResolution() {
//        return resolution;
//    }
//
//    public void setResolution(String resolution) {
//        this.resolution = resolution;
//    }
//
//    public String getResolvedBuild() {
//        return resolvedBuild;
//    }
//
//    public void setResolvedBuild(String resolvedBuild) {
//        this.resolvedBuild = resolvedBuild;
//    }
//
//    public String getResolvedDate() {
//        return resolvedDate;
//    }
//
//    public void setResolvedDate(String resolvedDate) {
//        this.resolvedDate = resolvedDate;
//    }
//
//    public ZentaoUserInfo getClosedBy() {
//        return closedBy;
//    }
//
//    public void setClosedBy(ZentaoUserInfo closedBy) {
//        this.closedBy = closedBy;
//    }
//
//    public String getClosedDate() {
//        return closedDate;
//    }
//
//    public void setClosedDate(String closedDate) {
//        this.closedDate = closedDate;
//    }
//
//    public int getDuplicateBug() {
//        return duplicateBug;
//    }
//
//    public void setDuplicateBug(int duplicateBug) {
//        this.duplicateBug = duplicateBug;
//    }
//
//    public String getLinkBug() {
//        return linkBug;
//    }
//
//    public void setLinkBug(String linkBug) {
//        this.linkBug = linkBug;
//    }
//
//    public int getCase1() {
//        return case1;
//    }
//
//    public void setCase1(int case1) {
//        this.case1 = case1;
//    }
//
//    public int getCaseVersion() {
//        return caseVersion;
//    }
//
//    public void setCaseVersion(int caseVersion) {
//        this.caseVersion = caseVersion;
//    }
//
//    public int getFeedback() {
//        return feedback;
//    }
//
//    public void setFeedback(int feedback) {
//        this.feedback = feedback;
//    }
//
//    public int getResult() {
//        return result;
//    }
//
//    public void setResult(int result) {
//        this.result = result;
//    }
//
//    public int getRepo() {
//        return repo;
//    }
//
//    public void setRepo(int repo) {
//        this.repo = repo;
//    }
//
//    public int getMr() {
//        return mr;
//    }
//
//    public void setMr(int mr) {
//        this.mr = mr;
//    }
//
//    public String getEntry() {
//        return entry;
//    }
//
//    public void setEntry(String entry) {
//        this.entry = entry;
//    }
//
//    public String getLines() {
//        return lines;
//    }
//
//    public void setLines(String lines) {
//        this.lines = lines;
//    }
//
//    public String getV1() {
//        return v1;
//    }
//
//    public void setV1(String v1) {
//        this.v1 = v1;
//    }
//
//    public String getV2() {
//        return v2;
//    }
//
//    public void setV2(String v2) {
//        this.v2 = v2;
//    }
//
//    public String getRepoType() {
//        return repoType;
//    }
//
//    public void setRepoType(String repoType) {
//        this.repoType = repoType;
//    }
//
//    public String getIssueKey() {
//        return issueKey;
//    }
//
//    public void setIssueKey(String issueKey) {
//        this.issueKey = issueKey;
//    }
//
//    public int getTesttask() {
//        return testtask;
//    }
//
//    public void setTesttask(int testtask) {
//        this.testtask = testtask;
//    }
//
//    public ZentaoUserInfo getLastEditedBy() {
//        return lastEditedBy;
//    }
//
//    public void setLastEditedBy(ZentaoUserInfo lastEditedBy) {
//        this.lastEditedBy = lastEditedBy;
//    }
//
//    public String getLastEditedDate() {
//        return lastEditedDate;
//    }
//
//    public void setLastEditedDate(String lastEditedDate) {
//        this.lastEditedDate = lastEditedDate;
//    }
//
//    public boolean isDeleted() {
//        return deleted;
//    }
//
//    public void setDeleted(boolean deleted) {
//        this.deleted = deleted;
//    }
//
//    public String getPriOrder() {
//        return priOrder;
//    }
//
//    public void setPriOrder(String priOrder) {
//        this.priOrder = priOrder;
//    }
//
//    public int getSeverityOrder() {
//        return severityOrder;
//    }
//
//    public void setSeverityOrder(int severityOrder) {
//        this.severityOrder = severityOrder;
//    }
//
//    public int getDelay() {
//        return delay;
//    }
//
//    public void setDelay(int delay) {
//        this.delay = delay;
//    }
//
//    public boolean isNeedconfirm() {
//        return needconfirm;
//    }
//
//    public void setNeedconfirm(boolean needconfirm) {
//        this.needconfirm = needconfirm;
//    }
//
//    public String getStatusName() {
//        return statusName;
//    }
//
//    public void setStatusName(String statusName) {
//        this.statusName = statusName;
//    }
//
//    public String getProductStatus() {
//        return productStatus;
//    }
//
//    public void setProductStatus(String productStatus) {
//        this.productStatus = productStatus;
//    }
//}
//
