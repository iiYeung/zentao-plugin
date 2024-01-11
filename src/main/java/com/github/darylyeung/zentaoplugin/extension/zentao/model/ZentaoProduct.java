package com.github.darylyeung.zentaoplugin.extension.zentao.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * @author Yeung
 * @version v1.0
 * @date 2024-01-11 22:25:14
 */
public class ZentaoProduct {
    private int id;
    private int program;
    private String name;
    private String code;
    private int shadow;
    private String bind;
    private int line;
    private String type;
    private String status;
    private String subStatus;
    private String desc;
    private ZentaoUserInfo PO;
    private ZentaoUserInfo QD;
    private ZentaoUserInfo RD;
    private String feedback;
    private String ticket;
    private String acl;
    private List<Object> whitelist;
    private String reviewer;
    private ZentaoUserInfo createdBy;
    private String createdDate;
    private String createdVersion;
    private int order;
    private String vision;
    private String deleted;
    private String lineName;
    private String programName;
    private String programPM;
    private Map<String, Integer> stories;
    private Map<String, Object> requirements;
    private int plans;
    private int releases;
    private int bugs;
    private int unResolved;
    private int closedBugs;
    private int fixedBugs;
    private int thisWeekBugs;
    private int assignToNull;
    private int progress;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProgram() {
        return program;
    }

    public void setProgram(int program) {
        this.program = program;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getShadow() {
        return shadow;
    }

    public void setShadow(int shadow) {
        this.shadow = shadow;
    }

    public String getBind() {
        return bind;
    }

    public void setBind(String bind) {
        this.bind = bind;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSubStatus() {
        return subStatus;
    }

    public void setSubStatus(String subStatus) {
        this.subStatus = subStatus;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public ZentaoUserInfo getPO() {
        return PO;
    }

    public void setPO(ZentaoUserInfo PO) {
        this.PO = PO;
    }

    public ZentaoUserInfo getQD() {
        return QD;
    }

    public void setQD(ZentaoUserInfo QD) {
        this.QD = QD;
    }

    public ZentaoUserInfo getRD() {
        return RD;
    }

    public void setRD(ZentaoUserInfo RD) {
        this.RD = RD;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public String getAcl() {
        return acl;
    }

    public void setAcl(String acl) {
        this.acl = acl;
    }

    public List<Object> getWhitelist() {
        return whitelist;
    }

    public void setWhitelist(List<Object> whitelist) {
        this.whitelist = whitelist;
    }

    public String getReviewer() {
        return reviewer;
    }

    public void setReviewer(String reviewer) {
        this.reviewer = reviewer;
    }

    public ZentaoUserInfo getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(ZentaoUserInfo createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getCreatedVersion() {
        return createdVersion;
    }

    public void setCreatedVersion(String createdVersion) {
        this.createdVersion = createdVersion;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getVision() {
        return vision;
    }

    public void setVision(String vision) {
        this.vision = vision;
    }

    public String getDeleted() {
        return deleted;
    }

    public void setDeleted(String deleted) {
        this.deleted = deleted;
    }

    public String getLineName() {
        return lineName;
    }

    public void setLineName(String lineName) {
        this.lineName = lineName;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public String getProgramPM() {
        return programPM;
    }

    public void setProgramPM(String programPM) {
        this.programPM = programPM;
    }

    public Map<String, Integer> getStories() {
        return stories;
    }

    public void setStories(Map<String, Integer> stories) {
        this.stories = stories;
    }

    public Map<String, Object> getRequirements() {
        return requirements;
    }

    public void setRequirements(Map<String, Object> requirements) {
        this.requirements = requirements;
    }

    public int getPlans() {
        return plans;
    }

    public void setPlans(int plans) {
        this.plans = plans;
    }

    public int getReleases() {
        return releases;
    }

    public void setReleases(int releases) {
        this.releases = releases;
    }

    public int getBugs() {
        return bugs;
    }

    public void setBugs(int bugs) {
        this.bugs = bugs;
    }

    public int getUnResolved() {
        return unResolved;
    }

    public void setUnResolved(int unResolved) {
        this.unResolved = unResolved;
    }

    public int getClosedBugs() {
        return closedBugs;
    }

    public void setClosedBugs(int closedBugs) {
        this.closedBugs = closedBugs;
    }

    public int getFixedBugs() {
        return fixedBugs;
    }

    public void setFixedBugs(int fixedBugs) {
        this.fixedBugs = fixedBugs;
    }

    public int getThisWeekBugs() {
        return thisWeekBugs;
    }

    public void setThisWeekBugs(int thisWeekBugs) {
        this.thisWeekBugs = thisWeekBugs;
    }

    public int getAssignToNull() {
        return assignToNull;
    }

    public void setAssignToNull(int assignToNull) {
        this.assignToNull = assignToNull;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}
