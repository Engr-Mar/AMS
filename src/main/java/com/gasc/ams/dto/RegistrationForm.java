package com.gasc.ams.dto;

import jakarta.persistence.Lob;

public class RegistrationForm {

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        return value
            .replace('\u00A0', ' ')  
            .replace('\u3000', ' ')  
            .trim();
    }
    private Integer assetId = null;
    private String computerName = "";
    private String macAddress = "";
    private String manufacturerName = "";
    private String modelNumber = "";
    private String os = "";
    private String office = "";
    private String securitySoftware = "";
    private String domain = "";

    private String cpu1 = "";
    private String cpu2 = "";
    private String cpu3 = "";

    private String memory = "";
    private String pcTypes = "";
    private String owned = "";
    private String purchaseDate = "";

    private String monitor = "";
    private Boolean keyboard = false;
    private Boolean mouse = false;
    private String others = "";

    private String location = "";
    private String floor = "";
    private String room = "";
    private String compUser = "";
    private String caseNumber = "";

    @Lob
    private String remarks = "";
    private String disposal = "";

    private Boolean disposalFlag = false;
    private String win10Priority = "";
    private Boolean exist2025 = false;

    public Integer getAssetId() { return assetId; }
    public void setAssetId(Integer assetId) { this.assetId = assetId; }

    public String getComputerName() { return computerName; }
    public void setComputerName(String computerName) { this.computerName = normalize(computerName); }

    public String getMacAddress() { return macAddress; }
    public void setMacAddress(String macAddress) { this.macAddress = normalize(macAddress); }

    public String getManufacturerName() { return manufacturerName; }
    public void setManufacturerName(String manufacturerName) { this.manufacturerName = normalize(manufacturerName); }

    public String getModelNumber() { return modelNumber; }
    public void setModelNumber(String modelNumber) { this.modelNumber = normalize(modelNumber); }

    public String getOs() { return os; }
    public void setOs(String os) { this.os = normalize(os); }

    public String getOffice() { return office; }
    public void setOffice(String office) { this.office = normalize(office); }

    public String getSecuritySoftware() { return securitySoftware; }
    public void setSecuritySoftware(String securitySoftware) { this.securitySoftware = normalize(securitySoftware); }

    public String getDomain() { return domain; }
    public void setDomain(String domain) { this.domain = normalize(domain); }

    public String getCpu1() { return cpu1; }
    public void setCpu1(String cpu1) { this.cpu1 = normalize(cpu1); }

    public String getCpu2() { return cpu2; }
    public void setCpu2(String cpu2) { this.cpu2 = normalize(cpu2); }

    public String getCpu3() { return cpu3; }
    public void setCpu3(String cpu3) { this.cpu3 = normalize(cpu3); }

    public String getMemory() { return memory; }
    public void setMemory(String memory) { this.memory = normalize(memory); }

    public String getPcTypes() { return pcTypes; }
    public void setPcTypes(String pcTypes) { this.pcTypes = normalize(pcTypes); }

    public String getOwned() { return owned; }
    public void setOwned(String owned) { this.owned = normalize(owned); }

    public String getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(String purchaseDate) { this.purchaseDate = normalize(purchaseDate); }

    public String getMonitor() { return monitor; }
    public void setMonitor(String monitor) { this.monitor = normalize(monitor); }

    public Boolean getKeyboard() { return keyboard; }
    public void setKeyboard(Boolean keyboard) { this.keyboard = keyboard; }

    public Boolean getMouse() { return mouse; }
    public void setMouse(Boolean mouse) { this.mouse = mouse; }

    public String getOthers() { return others; }
    public void setOthers(String others) { this.others = normalize(others); }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = normalize(location); }

    public String getFloor() { return floor; }
    public void setFloor(String floor) { this.floor = normalize(floor); }

    public String getRoom() { return room; }
    public void setRoom(String room) { this.room = normalize(room); }

    public String getCompUser() { return compUser; }
    public void setCompUser(String compUser) { this.compUser = normalize(compUser); }

    public String getCaseNumber() { return caseNumber; }
    public void setCaseNumber(String caseNumber) { this.caseNumber = normalize(caseNumber); }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = normalize(remarks); }

    public String getDisposal() { return disposal; }
    public void setDisposal(String disposal) { this.disposal = normalize(disposal); }

    public Boolean getDisposalFlag() { return disposalFlag; }
    public void setDisposalFlag(Boolean disposalFlag) { this.disposalFlag = disposalFlag; }

    public String getWin10Priority() { return win10Priority; }
    public void setWin10Priority(String win10Priority) { this.win10Priority = normalize(win10Priority); }

    public Boolean getExist2025() { return exist2025; }
    public void setExist2025(Boolean exist2025) { this.exist2025 = exist2025; }
}
