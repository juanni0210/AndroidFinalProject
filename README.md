# AndroidFinalProject
### This is a final project assignment for CST2335 Graphica Interface Programming. 
#### It is a group project of 4 memebers, each of us is reasponsible for a topic and finally integrated into a single working application.
* Our team members and responsible topics:
  * Feiqiong Deng - Soccer Game 
  * Juan Ni  - Trivia Game
  * Sophie Sun - Car Database
  * Xueru Chen - Songster Search
  
#### The Application Topics
##### Each of the applications (as they are intended) requires similar programming techniques.  Each application takes information from the user and stores it in a database. They can then view the data saved to a list of favourites and delete items from that list. Beyond that you are free to get creative.

#### Trivia API
* The user can generate a trivia game to play on their phone. The interface should ask for the number of questions, whether they want true/false, or multiple choice, or both, and whether the questions should be easy, medium, or hard.
* The API documentation is located here: https://opentdb.com/api_config.php. The URL pattern is: https://opentdb.com/api.php?amount=XXX&type=YYY&difficulty=ZZZ, XXX is an integer for the number of questions. YYY is either “Boolean” or “Multiple”, and ZZZ is either “EASY”, “MEDIUM”, or “HARD”
* The numbers should be shown in a list on the left hand side of whether the question is not yet answered, answered wrong, or answered right. That means that your question objects should have 3 possible states, unanswered, wrong, or right. At the beginning of the quiz, all questions are unanswered and at the end, they should all be either wrong or right.
* Once the questions are downloaded, you should start the trivia game. You should show each question to the user and give either True/False buttons to answer, or 1, 2, 3, 4 buttons for multiple choice. For each question, track the number of questions they answered correctly and show the result after they have answered all the questions. You should then ask for the person’s name and store their results in a database. This name dialog should save the previous name entered in SharedPreferences so that the user doesn’t have to type it again the next time
* There should be a high score screen that shows the previous trivia scores, the names of the players, and whether they were playing easy, medium, or hard questions. There should be an option for removing individual previous high scores from the database.

#### Songster search page
* The user can type in an artist or band name and get a list of songs that they played. The resulting list should be shown on the left hand side. Clicking on an individual song should show details about that song:
* The URL pattern is:  http://www.songsterr.com/a/ra/songs.xml?pattern=XXX, where XXX is the band or artist. You can replace the .xml in songs.xml with .json (songs.json) to get a JSON response of XML. This will return a list of songs that match the song name. Each <Song> tag should have an id= parameter, and an <Artist> tag also with an artist id= parameter.
* Clicking on one of the songs in the list should show the artist id, song id, and title in the fragment. There should be a button “Save to favourites” which saves the song, and artist id to the database. Clicking on the Song id should launch a browser to the song’s guitar music page, which is accessible using the URL pattern: http://www.songsterr.com/a/wa/song?id=XXX where XXX is the song ID. Clicking on the Artist id should launch a web browser to the artist’s list of songs which is accessible using the URL pattern: http://www.songsterr.com/a/wa/artist?id=XXX, where XXX is the artist’s id.
* There should be a “favourites” button that shows a list of saved songs. Selecting a song from the list should show the same fragment as before with the artist and song id, and buttons loading web browsers to that song. However the “Save to favourites” button should now be a “Remove from favourites” button.
* You should use SharedPreferences to save the search term that the user entered for the next time you launch the application.

#### Car database
* The user can type in a car manufacturer (Honda, Toyota, Tesla, Ford, etc.) and get a list of models made by that company. The URL pattern is: https://vpic.nhtsa.dot.gov/api/vehicles/GetModelsForMake/XXX?format=YYY, where XXX is the company name, and YYY is JSON or XML depending on what format of data you want.
* The results should be put in a list on the left hand side. Whenever a user selects on a car, the name of that model should be shown in a fragment using the Model_ID. There should be a button for saving the car details in the database. There should be a button for viewing car details, and another button for shopping for the car.
* To view a car details, just launch a web browser to do a google search: http://www.google.com/search?q=YYY+ZZZ where YYY is the make, and ZZZ is the model.
* To shop for the car, launch an Autotrader.com search using the URL pattern: https://www.autotrader.ca/cars/?mdl=ZZZ&make=YYY&loc=K2G1V8, where ZZZ is the model, and YYY is the make, and K2G1V8 is Algonquin College’s postal code. For this project, we only want cars for sale near the college.
* There should be a button for viewing the list of cars saved in the database, and when you click on a car, it shows the same details page as above, but instead the “save to database” button is replaced with a “remove from database” button.
* Use SharedPreferences to save the last query a car manufacturer so you don’t have to type it in again.

#### Soccer games api
* The user can download a list of soccer news articles from http://www.goal.com/en/feeds/news?fmt=rss. This will have a list of <item> tags which have a title, date, and a thumbnail image. These news articles should be shown in a list on the left hand side. Clicking on a title should show the image, date, news article URL and description text. There should be a button to save the article to a favourites database, and another button to load the article’s URL in a browser. 
* There should be a button to show the saved news articles in a list, like above. When you select a saved news article, it shows the same details as above, the instead of a “save” button, it is replaced with a “remove from favourites” button which removes the story from the database.
* When you first start your application, there should be a dialog box which asks to rate the application using 5 stars. You should save the rating in SharedPreferences to show the next time you start the application.
