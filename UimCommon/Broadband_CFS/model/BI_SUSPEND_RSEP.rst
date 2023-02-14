<?xml version="1.0" encoding="UTF-8"?>
<com:modelEntity xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:com="http://www.mslv.com/studio/core/model/common" xmlns:inv="http://www.mslv.com/studio/inventory/model/specification" xmlns="http://www.mslv.com/studio/inventory/model/specification" xsi:type="inv:RuleTriggerType" name="BI_SUSPEND_RSEP">
  <com:saveVersion>5</com:saveVersion>
  <com:id>Ar2N3iBCQmmdfP-creZUHQ</com:id>
  <inv:placement>INSTEAD</inv:placement>
  <inv:point>
    <com:entity>ServiceManager_suspendService</com:entity>
    <com:entityType>rstp</com:entityType>
    <com:relationship>com.mslv.studio.inventory.ruleset.trigger.REL_POINT</com:relationship>
  </inv:point>
  <inv:ruleset>
    <com:entity>BI_SUSPEND_RS</com:entity>
    <com:entityType>ruleset</com:entityType>
    <com:relationship>com.mslv.studio.inventory.ruleset.trigger.REL_RULESET</com:relationship>
  </inv:ruleset>
</com:modelEntity>