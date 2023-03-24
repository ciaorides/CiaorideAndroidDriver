package com.ciaorides.ciaorides.utils

object TempConstants {
    const val PROMOTIONS_URL =
        "https://imageio.forbes.com/specials-images/imageserve/5d35eacaf1176b0008974b54/2020-Chevrolet-Corvette-Stingray/0x0.jpg?format=jpg&crop=4560,2565,x790,y784,safe&width=960"

   // const val USER_ID = 2117
    const val TYPE = "recent"
    const val MODE = "city"
    const val FROM_LAT = "17.386673"
    const val from_lng = "78.3810183"

    fun recentSearches(): ArrayList<String> {
        return ArrayList<String>().apply {
            add("Raidurg Metro station")
            add("Madhapur Petrol pump")
            add("Kukatpalli Village, beside BJP")
        }
    }
}
