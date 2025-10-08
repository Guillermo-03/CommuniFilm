# CommuniFilm
## A film discussion forum built with Spring Boot and Next.js, letting users share opinions on everything cinema from the latest releases to classic favorites.

CommuniFilm lets users discuss movies in a forum-style platform, with each post linked directly to its film. Beyond discussion, users can personalize profiles, showcase their top films, and discover new titles through AI-powered recommendations.

### Features
* Search for movies and view discussion threads  
* Post new opinions or reply to others  
* AI-based movie recommendations tailored to your tastes  
* Create and customize a profile with a bio, followers/following, and a top 10 films ranking  
* Discover and add friends to grow your movie discussion network  

---

### Contributors
- Mowei Zhang
- Guillermo R.  

---

## Core Features for Milestone 1

* **Google Authentication:** Secure sign-up and login using Google OAuth 2.0.
* **Movie Search:** Real-time movie search powered by the TMDB API.
* **Movie Details:** A detailed view for each movie, including posters, ratings, and release dates.
* **User Reviews:** Logged-in users can post reviews for movies.
* **User Profiles:** A "complete sign-up" flow for new users to add a bio.

---

## Development with AI Collaboration

This project was built iteratively with Google Gemini acting as a pair programmer. Instead of just asking for large chunks of code, the development process focused on a conversational flow of building, refining, and debugging. The AI was instrumental in:

* **Generating Boilerplate:** Creating initial files for models, services, controllers, and components based on feature requirements.
* **Refactoring Code:** Improving code quality by identifying duplication and suggesting more efficient patterns.
* **Troubleshooting & Debugging:** Diagnosing complex issues across the full stack, from backend security configurations to frontend data-fetching bugs.
* **Architectural Guidance:** Providing advice on best practices, such as project structure, state management, and API design.

### Key AI Prompts: Examples of Effective Collaboration

Progress was fastest when prompts were specific and targeted, as vague requests often led to generic code, while precise questions led to more targeted solutions.

Here are a few examples of constructive prompts that drove key breakthroughs:

1.  **For Workflow Architecture:**
    > "The workflow will most likely be: user sign in with google for the first time, backend saves new user document to firestore but without bio yet, then frontend shows a 'complete sign up' page... If he is not a new user, we do not show that page. Can you help see which files or functions in our current backend app needs change for the above?"
    * **Why it was effective:** This led to the creation of a `LoginResponseDto` to communicate a user's status (`isNewUser`) to the frontend, enabling a conditional redirect to a profile completion page—a core feature of the user experience
    
2. **For Refactoring & Code Quality:**
    > "could you refactor searchMovies() and getTrendy() so that they can share the response processing part of the codes instead of duplicating them"
    * **Why it was effective:** This prompt didn't just ask to "make the code better." It identified a specific code smell (duplication) and suggested a clear goal (a shared helper method), leading to cleaner, more maintainable service-layer code.

2.  **For API & Data Logic:**
    > "For posting a movie review, does the request body require a reviewId?" followed by "can you change the createReview method to use server timestamp instead of Instant.now() for createdAt and updatedAt"
    * **Why it was effective:** This sequence demonstrates a useful trick in prompting. The first question challenged the initial design (client-generated IDs) by posting a question and guided AI to self-correct a previous approach, and the follow-up prompt refined the logic to align with best practices (server-generated timestamps), making the API more robust and secure.

3.  **For Component Architecture:**
    > "can you combine both needs [full-page and inline spinners] into the spinner components and the importer page will choose which to render based on params"
    * **Why it was effective:** Instead of having two separate loading indicators, this prompt suggested creating a single, reusable component with props (`fullPage`, `size`). This led to a more flexible and DRY (Don't Repeat Yourself) component library.

---

## Debugging Journey: Lessons Learned

Troubleshooting is a core part of development. Here are some of the key bugs we encountered and how they were resolved through debugging using Gemini.

### 1. The CORS Issue

* **Symptom:** The frontend would receive a CORS error when making `PUT` requests (like saving a profile) or when sending an `Authorization` header, even to public endpoints.
* **Debugging Process:**
    1.  Initially, we tried adding a global CORS mapping in a `WebMvcConfigurer`, but this conflicted with Spring Security.
    2.  We then moved the CORS configuration into the `SecurityFilterChain`, which correctly handled the preflight `OPTIONS` requests and resolved the issue for all API calls.
* **Lesson:** When using Spring Security, it should be the single source of truth for CORS configuration to avoid filter chain conflicts.

### 2. The Case of the Mismatched Keys (snake_case vs. camelCase)

* **Symptom:** The frontend would crash with a `TypeError: Cannot read properties of undefined (reading 'toFixed')` on the movie detail page. The backend was sending JSON with `release_date`, but the frontend code was expecting `releaseDate`.
* **Debugging Process:**
    1.  We provided the AI with the exact JSON payload from the browser's network tab.
    2.  This confirmed a mismatch between the backend's output and the frontend's expectations.
    3.  The final solution was to add `@JsonProperty` annotations to the getters in the backend DTO, giving us explicit control over the final JSON output and ensuring `camelCase` keys were sent to the frontend.
* **Lesson:** Always verify the exact JSON payload being sent and received. Annotations provide precise control over serialization when default behavior isn't sufficient.

### 3. The Invisible Security Filter

* **Symptom:** A 403 Forbidden error occurred on authenticated routes, but our debug logs in the `GoogleAuthFilter` were never printing.
* **Debugging Process:**
    1.  The absence of logs indicated that the filter was not running at all.
    2.  We discovered that Spring's component scanning was not correctly wiring the `@Component`-annotated filter into the security chain.
    3.  The fix was to remove the `@Component` annotation and instead instantiate the filter directly within the `SecurityConfig` (`.addFilterBefore(new GoogleAuthFilter(googleAuthService), ...)`).
* **Lesson:** In Spring Security, explicitly defining beans and their relationships within the configuration class is often more reliable than relying solely on component scanning.