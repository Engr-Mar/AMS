package com.gasc.ams.dto;


public class SearchForm {


    private String compName = "";
    private Boolean compNameExact = false;

    private Boolean desktop = false;
    private Boolean laptop = false;
    private Boolean server = false;
    private Boolean other = false;


    private String manufacturer = "";
    private String model = "";
    private Boolean modelExact = false;
    private String macAddress = "";
    private Boolean macAddressExact = false;

    private String cpumaker = "";
    private String cpugeneration = "";
    private String cpumodel = "";
    private Boolean cpumodelExact = false;


    private Boolean lessthan2 = false;
    private Boolean between2and4 = false;
    private Boolean between4and8 = false;
    private Boolean between8and16 = false;
    private Boolean between16and32 = false;
    private Boolean morethan32 = false;


    private String windowssys = "";
    private String officesoft = "";
    private String securitysoft = "";

    private Boolean green = false;
    private Boolean inter = false;

    private String purdatefrom = "";
    private String purdateto = "";

    private String monitor = "";

    private Boolean withkeyb = false;
    private Boolean nokeyb = false;
    private Boolean withmouse = false;
    private Boolean nomouse = false;

    private Boolean loc1 = false;
    private Boolean loc2 = false;
    private Boolean loc3 = false;
    private Boolean loc4 = false;
    private Boolean floor4 = false;
    private Boolean floor7 = false;
    private String room = "";

    private String user = "";
    private Boolean userExact = false;

    private String project = "";
    private String status = "";
    private String statuschangeFrom = "";
    private String statuschangeTo = "";

    private Boolean win1 = false;
    private Boolean win2 = false;
    private Boolean win3 = false;
    private Boolean win4 = false;
    private Boolean win5 = false;


    public String getCompName() { return compName; }
    public void setCompName(String compName) { this.compName = compName; }

    public Boolean getCompNameExact() { return compNameExact; }
    public void setCompNameExact(Boolean compNameExact) { this.compNameExact = compNameExact; }

    public Boolean getDesktop() { return desktop; }
    public void setDesktop(Boolean desktop) { this.desktop = desktop; }

    public Boolean getLaptop() { return laptop; }
    public void setLaptop(Boolean laptop) { this.laptop = laptop; }

    public Boolean getServer() { return server; }
    public void setServer(Boolean server) { this.server = server; }

    public Boolean getOther() { return other; }
    public void setOther(Boolean other) { this.other = other; }

    public String getManufacturer() { return manufacturer; }
    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public Boolean getModelExact() { return modelExact; }
    public void setModelExact(Boolean modelExact) { this.modelExact = modelExact; }

    public String getMacAddress() { return macAddress; }
    public void setMacAddress(String macAddress) { this.macAddress = macAddress; }

    public Boolean getMacAddressExact() { return macAddressExact; }
    public void setMacAddressExact(Boolean macAddressExact) { this.macAddressExact = macAddressExact; }

    public String getCpumaker() { return cpumaker; }
    public void setCpumaker(String cpumaker) { this.cpumaker = cpumaker; }

    public String getCpugeneration() { return cpugeneration; }
    public void setCpugeneration(String cpugeneration) { this.cpugeneration = cpugeneration; }

    public String getCpumodel() { return cpumodel; }
    public void setCpumodel(String cpumodel) { this.cpumodel = cpumodel; }

    public Boolean getCpumodelExact() { return cpumodelExact; }
    public void setCpumodelExact(Boolean cpumodelExact) { this.cpumodelExact = cpumodelExact; }

    public Boolean getLessthan2() { return lessthan2; }
    public void setLessthan2(Boolean lessthan2) { this.lessthan2 = lessthan2; }

    public Boolean getBetween2and4() { return between2and4; }
    public void setBetween2and4(Boolean between2and4) { this.between2and4 = between2and4; }

    public Boolean getBetween4and8() { return between4and8; }
    public void setBetween4and8(Boolean between4and8) { this.between4and8 = between4and8; }

    public Boolean getBetween8and16() { return between8and16; }
    public void setBetween8and16(Boolean between8and16) { this.between8and16 = between8and16; }

    public Boolean getBetween16and32() { return between16and32; }
    public void setBetween16and32(Boolean between16and32) { this.between16and32 = between16and32; }

    public Boolean getMorethan32() { return morethan32; }
    public void setMorethan32(Boolean morethan32) { this.morethan32 = morethan32; }

    public String getWindowssys() { return windowssys; }
    public void setWindowssys(String windowssys) { this.windowssys = windowssys; }

    public String getOfficesoft() { return officesoft; }
    public void setOfficesoft(String officesoft) { this.officesoft = officesoft; }

    public String getSecuritysoft() { return securitysoft; }
    public void setSecuritysoft(String securitysoft) { this.securitysoft = securitysoft; }

    public Boolean getGreen() { return green; }
    public void setGreen(Boolean green) { this.green = green; }

    public Boolean getInter() { return inter; }
    public void setInter(Boolean inter) { this.inter = inter; }

    public String getPurdatefrom() { return purdatefrom; }
    public void setPurdatefrom(String purdatefrom) { this.purdatefrom = purdatefrom; }

    public String getPurdateto() { return purdateto; }
    public void setPurdateto(String purdateto) { this.purdateto = purdateto; }

    public String getMonitor() { return monitor; }
    public void setMonitor(String monitor) { this.monitor = monitor; }

    public Boolean getWithkeyb() { return withkeyb; }
    public void setWithkeyb(Boolean withkeyb) { this.withkeyb = withkeyb; }

    public Boolean getNokeyb() { return nokeyb; }
    public void setNokeyb(Boolean nokeyb) { this.nokeyb = nokeyb; }

    public Boolean getWithmouse() { return withmouse; }
    public void setWithmouse(Boolean withmouse) { this.withmouse = withmouse; }

    public Boolean getNomouse() { return nomouse; }
    public void setNomouse(Boolean nomouse) { this.nomouse = nomouse; }

    public Boolean getLoc1() { return loc1; }
    public void setLoc1(Boolean loc1) { this.loc1 = loc1; }

    public Boolean getLoc2() { return loc2; }
    public void setLoc2(Boolean loc2) { this.loc2 = loc2; }

    public Boolean getLoc3() { return loc3; }
    public void setLoc3(Boolean loc3) { this.loc3 = loc3; }

    public Boolean getLoc4() { return loc4; }
    public void setLoc4(Boolean loc4) { this.loc4 = loc4; }

    public Boolean getFloor4() { return floor4; }
    public void setFloor4(Boolean floor4) { this.floor4 = floor4; }

    public Boolean getFloor7() { return floor7; }
    public void setFloor7(Boolean floor7) { this.floor7 = floor7; }

    public String getRoom() { return room; }
    public void setRoom(String room) { this.room = room; }

    public String getUser() { return user; }
    public void setUser(String user) { this.user = user; }

    public Boolean getUserExact() { return userExact; }
    public void setUserExact(Boolean userExact) { this.userExact = userExact; }

    public String getProject() { return project; }
    public void setProject(String project) { this.project = project; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getStatuschangeFrom() { return statuschangeFrom; }
    public void setStatuschangeFrom(String statuschangeFrom) { this.statuschangeFrom = statuschangeFrom; }

    public String getStatuschangeTo() { return statuschangeTo; }
    public void setStatuschangeTo(String statuschangeTo) { this.statuschangeTo = statuschangeTo; }

    public Boolean getWin1() {return win1;}

    public void setWin1(Boolean win1) {this.win1 = win1;}

    public Boolean getWin2() { return win2;}

    public void setWin2(Boolean win2) {this.win2 = win2; }

    public Boolean getWin3() {return win3;}

    public void setWin3(Boolean win3) {this.win3 = win3;}

    public Boolean getWin4() {return win4; }

    public void setWin4(Boolean win4) {this.win4 = win4;}

    public Boolean getWin5() {return win5;}

    public void setWin5(Boolean win5) {this.win5 = win5;}
}
