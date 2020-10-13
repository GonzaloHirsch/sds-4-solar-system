seconds = 24294250
minutes = seconds/60
hours = minutes/60
days = hours/24

left_days = days - int(days)
tot_hours = left_days*24
left_hours = tot_hours - int(tot_hours)
tot_minutes = left_hours*60
left_minutes = tot_minutes - int(tot_minutes)
tot_secs = left_minutes*60
left_secs = tot_secs - int(tot_secs)

data = str(int(days)) + "d " + str(int(tot_hours)) + "h " + str(int(tot_minutes)) + "m " + str(int(tot_secs)) + "s"
print(data)