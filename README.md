##Task: 
To develop a program for Android – Search for locations and possibility to see the locations on the map.

###Description:
The application on getting the text from input line makes a request to Google Geocode API servers, gets a list of locations, parses them, forms the list in the form of Grid consisting of 2 elements in a row. Each element of the list includes an image (a fragment of the map with the location (a scheme)) and the address of the location. Text information is at the bottom of the element against a black transparent background. The lists and images must be cached. For the lists cache is in the form of SQLite Data Base. For images a two-level cache (memory + file system) should be provided. Tapping on the element of the list leads to the display of the location on Google Maps in the form of a marker. Automatical loading of elements into the lists every 1-2 seconds after texting should be provided. It’s IMPORTANT that there should be no requests at the moment of texting unless the interval between pressing buttons exceeds the one mentioned above (1-2 seconds).

###Requirements:
* Android 4.0+
* Indentation in the UI should be equal.
* Proper use of libraries is encouraged.
