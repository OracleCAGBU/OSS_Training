

import oracle.communications.inventory.api.entity.*;
import oracle.communications.inventory.api.entity.common.Reservation;
import oracle.communications.inventory.api.framework.logging.Log;
import oracle.communications.inventory.extensibility.extension.util.*;


log.debug ("", "Rule Reservation Expiration - Set default short term expiry")
Binding binding=this.getBinding();
Map<String,Object> map= binding.getVariables();
if(map.get("extensionPointRuleContext")!=null)
{
ExtensionPointRuleContext extensionPointRuleContext=binding.getVariable("extensionPointRuleContext");
List reservations = (List)extensionPointRuleContext.getArguments()[0];
GregorianCalendar shortTermExpiry = new GregorianCalendar();
int shortTermDurationInMinutes = 10;
shortTermExpiry.add(GregorianCalendar.MINUTE, shortTermDurationInMinutes);
GregorianCalendar longTermExpiry = new GregorianCalendar();
int longTermDurationInDays = 40;
longTermExpiry.add(GregorianCalendar.DAY_OF_MONTH, longTermDurationInDays);
for( Object object: reservations){
	Reservation reservation = (Reservation)object;
	if(reservation.getReservationType().equals(ReservationType.SHORTTERM))
		reservation.setExpiry(shortTermExpiry.getTime());
	else
		reservation.setExpiry(longTermExpiry.getTime());
}
}