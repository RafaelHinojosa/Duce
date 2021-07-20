# Duce

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
2. [Resources](#Resources)
3. [Wireframes](#Wireframes)
4. [Schema](#Schema)
5. [API](#APIs)

## Overview
### Description
Duce is a communication app that allow users to communicate in their own language with people with another. The user can chat with friends and random users from all over the world while reading their messages in the user's language and the original one. The random user algorithm will give you options to chat with users according to your languages and interests. 

### App Evaluation
[Evaluation of your app across the following attributes]
- **Category:** Communications
- **Mobile:** The app will be developed in Android Studio for mobile Android devices.  
- **Story:** The sender user can send messages in his/her own language to his/her friends or random users and the destination user will be able to read what the sender user wrote translated to his/her own language. 
- **Market:** Any individual could use this app, because is a communication app that connects people from different languages.
- **Habit:** It can be used monthly, weekly or daily. It is up to the user. 
- **Scope:** 
    - The scope will be determined in 4 steps: implementing messages with, friendships, a mathing algorithm and optionally, a videocall feature. 

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

* **User**
* Sign Up
* Log In
* Log Out

-- 

* **Messages**
* CRUD: Create, Read, Update, Delete
* Chats Menu:
    * Create a Conversation
    * Read list of conversations
    * Read a specific conversation (goes to the chat)
    * Delete a Conversation
* Chat: 
    * Create a Message (send) 
    * Read messages (sent and received)
    * Update the receiving messages language (Settings)
    * Change between original and translated message (put a button at the end of each received message to change between original langugage and translated one)
    * Delete messages (sent and received)
   
--

* **Searching**
* Search users by:
    * First Language
    * Languages of Interest
    * Language proficiency between languages of both (greater first)
    * Age +- number
    * Country 
* Consult Matching Generator:
    Algo bases on:
    * First Language
    * Languages of interest
    * Age +- 5
    * Country
* Go to a user's profile
* Message a user from...
    * Finder
    * User's profile

--

* **Profile** 
* Save/See/Edit/Delete...
    *  Profile picture
    *  Username 
    *  Password 
    *  Self Description
    *  First Languages (5 points in proficiency)
    *  Second/Interest Languages (0 - 4 in proficiency)
    *  Country
    *  Date of birth
* See other users profiles
    * Start/continue with conversation

--

* **Friends**
* Send/Accept/Decline Friend Request 
* See my friends
* See other user's friends
* Delete friendship


**Optional Nice-to-have Stories**
* **Messages**
* Send audios
* Receive audios
    * See/Hide transcription and translation
* Translate and transcribe a selected audio
* When creating a chat, can select multiple participants and hence, select a name for the conversation...
* Add more participants to the chat

--

* **VideoCall** 
* Call a user
* Accept call
* Decline call
* OnCall Activities:
    * Select Sending Language for voice and text
    * Select Receiving Language for text
    * On/Off Mic/Video/Captions/Screen Messaging
* End call (gestures)
* Flip camera (tap on video for 2 seconds?)
* Drag and drop text boxes
* Select chat text language
* Send Text (onCall act. and may be translated)
* See recieved text
* Show cards with random questions to start a conversation.

--

* **Profile**
* Save/Edit/Delete 
    * Languages of Interest
* See User profile 
    * Register rating for the user
* Rate another user's language

--

* **Charts**
* % of sent messages in different languages
* % of received messages in different languages 

-- 

* **Searching**
* MatchGen Algo
    * Match Languages of Interest
    * User sets the parameters for doing the match

--

* **Friends**
* See common friends between other users and me

### 2. Screen Archetypes

* [list first screen here]
   * [list associated required story here]
   * ...
* [list second screen here]
   * [list associated required story here]
   * ...

### 3. Navigation

**Tab Navigation** (Tab to Screen)
* Messages
* Finder 
* Profile
    * My Profile 
    * Friends

**Flow Navigation** (Screen to Screen)

* Start
    * Log In
    * Sign Up
* Log In
    * Finder
* Sign Up
    * Profile
--
* Finder
    * Profile
    * Video Call
    * Profile [Searched one]
    * Messages
    * Conversation
--
* Profile
    * Finder
    * Profile Edit
    * My Profile
    * Friends
    * Messages
* Profile Edit
    * Finder
    * Profile
    * My Profile
    * Friends
    * Messages
--
* Chats 
    * Finder
    * Profile
    * Conversation
* Conversation
    * Finder
    * Profile
    * Messages
    * Conversation + Options
* Conversation + Options
    * Finder
    * Profile
    * Messages
    * Conversation

-- Stretch
* Video Call
    * Video Call Actions
* Video Call Actions
    * Video Call
    * Video Call Settings
* Video Call Settings
    * Video Call Actions
    
### 4. APIs 
* **Tranlate Text**
* Google Translate: https://cloud.google.com/translate
* 
* **Speech Dictation**
* Google Speech to Text: https://cloud.google.com/speech-to-text
* 
* **VideoCall**
* Twilio: https://www.twilio.com/console


### 5. Frameworks for Polishing
* Animations: https://github.com/airbnb/lottie-android
* Toasts: https://github.com/Muddz/StyleableToast
* Color Palette Info: https://sympli.io/blog/5-apps-to-help-you-choose-mesmerising-color-schemes/
* More info: https://www.freecodecamp.org/news/25-new-android-libraries-which-you-definitely-want-to-try-at-the-beginning-of-2017-45878d5408c0/
* Polishing Tips: https://guides.codepath.com/android/Polishing-a-UI-Tips-and-Tools

## Animations
* [MVP] Login (maybe put a welcome message with a hand)
* [Stretch] Charging Fire/Flag when MatchGen Algo is exectued 
* [Stretch] Sending a friend request (something como brillos o destellos)

### 6. Wireframes
https://www.figma.com/file/uk3pFzBBJkdWrekxCNawN7/Duce?node-id=0%3A1
<img src="YOUR_WIREFRAME_IMAGE_URL" width=600>
![](https://imgur.com/bsn26NO.png)

### [BONUS] Digital Wireframes & Mockups

### [BONUS] Interactive Prototype
https://www.figma.com/proto/uk3pFzBBJkdWrekxCNawN7/Duce?node-id=1%3A2&scaling=scale-down&page-id=0%3A1&starting-point-node-id=1%3A2

## Schema 
[This section will be completed in Unit 9]

### Models
**Users**
| Property            | Type                     | Description |
| --------            | --------                 | --------    |
| user_id             | String                   | unique id for the user (**default** field)|
| username            | String                   | unique name for the user | 
| password            | String                   | password to log in into the app | 
| createdAt           | Date                     | Date of registration of the user in |
| birthday            | DateTime                 | with this we'll calculate the age |
| profile_picture     | File                     | user's image to identify itself | 
| self_description    | String                   | will appear on the profile | 
| countryId           | Pointer to [Countries]   | where the user is from | 
N(users) * 1(country) Relationship


**Countries**
| Property         | Type             | Description |
| --------         | --------         | --------    |
| country_id       | String           | unique id for the country (**default** field)|
| country_name     | String           | unique name for the country | 
| country_flag     | String           | use emoji library for using it: https://developer.android.com/guide/topics/ui/look-and-feel/emoji-compat | 

N(users) * 1(country)


**Languages**
| Property         | Type             | Description |
| --------         | --------         | --------    |
| language_id      | String           | unique id for the language (**default** field)|
| language_name    | String           | unique name for the user | 


**User_Languages**
| Property         | Type                     | Description |
| --------         | --------                 | --------    |
| user_language_id | String                   | unique id for the user and language relationship (**default** field) |
| user_id          | Pointer to [User]        | the id of the user that known the language | 
| language_id      | Pointer to [Languages]   | the id of the language known | 
| proficency       | Integer                  | From 0 - 5 https://www.indeed.com/career-advice/resumes-cover-letters/levels-of-fluency-resume | 
| interested_in    | Boolean                  | true if the user is interested in the language | 

N(users) * N(languages) relationship

**Friends**
| Property         | Type                 | Description |
| --------         | --------             | --------    |
| friends_id       | String               | unique id for the friend relationship (**default** field)|
| friend1          | Pointer to [User]    | the id of the first of 2 friends | 
| friend2          | Pointer to [User]    | the id of the second of 2 friends | 


**Messages**
| Property         | Type                     | Description |
| --------         | --------                 | --------    |
| conversation_id  | String                   | unique id for the conversation(**default** field) |
| sender_id        | Pointer to [User]        | Sender Id | 
| receiver_id      | Pointer to [User]        | Receiver Id |
| description      | String                   | The message per-se |
| createdAt        | DateTime                 | The date the message was sent |
| audio_msg        | File                     | The audio the user sent | 


### Networking
- [Add list of network requests by screen ]
- [Create basic snippets for each Parse network request]
- [OPTIONAL: List endpoints]


