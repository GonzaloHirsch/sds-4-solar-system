import math

# We assume the Earth is in the TOP-RIGHT quadrant (x+ y+ quadrant) in order to perform calculation

# Earth data
EARTH_X_POS = 1.493188929636662 * (10**8)
EARTH_Y_POS = 1.318936357931255 * (10**7)
EARTH_X_VEL = -3.113279917782445
EARTH_Y_VEL = 2.955205189256462 * 10
EARTH_RADIUS = 6371.01
SHIP_ORBITAL_DISTANCE = 1500
SHIP_ORBITAL_VELOCITY = 7.12

# Calculating angle of position
alpha = math.atan2(EARTH_Y_POS, EARTH_X_POS)

# Calculating distance to the sun
earth_distance_to_sun = math.sqrt(EARTH_X_POS**2 + EARTH_Y_POS**2)

# Calculating ship's distance to the sun
ship_distance_to_sun = earth_distance_to_sun + SHIP_ORBITAL_DISTANCE + EARTH_RADIUS

# Calculating ship's position components
ship_x_pos = math.cos(alpha) * ship_distance_to_sun
ship_y_pos = math.sin(alpha) * ship_distance_to_sun

# Calculating velocity components
ship_x_vel = math.sin(alpha) * SHIP_ORBITAL_VELOCITY + EARTH_X_VEL
ship_y_vel = math.cos(alpha) * SHIP_ORBITAL_VELOCITY + EARTH_Y_VEL

print("Data for spaceship is:")
print("X POSITION:", ship_x_pos)
print("Y POSITION:", ship_y_pos)
print("X VELOCITY:", ship_x_vel)
print("Y VELOCITY:", ship_y_vel)