# TransFolio – Football Transfer News Tracker ⚽

**TransFolio** is an AI-enhanced web app designed to help football fans stay on top of the transfer market. In a fast-moving world of player moves, TransFolio aggregates the latest transfer news and uses **Google’s Gemini LLM** to generate concise, fan-friendly summaries.

Users can register, pick their favorite clubs, and receive a personalized feed of confirmed arrivals and departures. The backend (Java/Spring Boot) schedules daily data fetches and even sends email based on each user’s preferences—keeping fans informed without the noise.

---

## 🌟 Features

- 🔐 **User Accounts**: Secure registration and login system to create a personalized experience.
- ⚽ **Club Preferences**: Search and follow your favorite teams. TransFolio filters news so you only see transfers relevant to your clubs.
- 🤖 **AI-Powered Summaries**: Uses Google’s Gemini API to rewrite transfer news in a lively, fan-friendly tone (think *“Visca Barça!”*). Articles are condensed into quick, digestible updates.
- ✉️ **Email Notifications**: A scheduled Spring Boot task automatically emails users the latest transfer news each day, based on their selected clubs.
- 🔍 **Real-Time Search**: Instantly look up transfers by club name.
- 📈 **Live News Feed**: Continuously updated feed of confirmed transfers and rumors, summarized with AI-generated commentary.

---

## 🛠 Tech Stack

- **Java & Spring Boot**: Backend server with `@Scheduled` tasks for periodic data fetching and email notifications.
- **PostgreSQL (Render)**: Stores users, preferences, and news. Hosted on Render for scalability.
- **React**: Minimalist UI for an interactive, stateful experience.
- **Gemini API**: Google’s Generative Language Model is used for summarization.
- **Vercel**: Hosting platform for the React frontend.
- **SMTP (e.g., SendGrid)**: Sends automated email alerts to users.

---

## ✨ Try It Now!

👉 [Live Site](https://transfolio.vercel.app)  
👉 [Backend API](https://render.com/)  
👉 [GitHub Repo](https://github.com/ASRoshanKrishna/transfolio)

---

## ⚙️ How It Works

1. **User Setup**: Fans register and log in. On the dashboard, they search and mark clubs as "favorites."
2. **Data Aggregation**: A daily scheduled job fetches the latest transfer news from APIs or RSS feeds and stores it in PostgreSQL.
3. **AI Summarization**: Raw articles are sent to the Gemini API, which returns a fan-style summary.
    - _Example_:
      > ⚡ **BARÇA BOOST!** Goalkeeper Joan Garcia is heading to FC Barcelona for €25M. Permanent move – get ready to see him between the sticks! *Visca Barça!*
4. **Personalized Feed**: Summarized news is shown in the React UI, filtered by the user's selected clubs.
5. **Email Notification**: Users receive email alerts whenever there’s new transfer news or rumors related to their selected clubs.
---

## 🖼 Screenshots

- **Login Page**: Users can register or log in to access their personalized dashboard.

![Image](https://github.com/user-attachments/assets/e0c372be-e567-461c-9a07-faa5904947d3)

- **Club Search**: Example search for “Madrid,” adding Real Madrid to favorites.

![Image](https://github.com/user-attachments/assets/7172427f-c077-437b-84cb-0785843fd804)

- **News Feed**: Shows player, club, type (arrival/departure), fee, and AI-generated summary.

![Image](https://github.com/user-attachments/assets/ea77661c-1e9a-4492-850b-ba2e753cfc98)

---

## 🔮 Future Improvements & Known Limitations

- ⏳ **Summary Latency**: Free-tier Gemini API can take a few seconds per article. Users may need to refresh to see updates.
- 🚧 **API Rate Limits**: Limited to ~100 calls/day with Transfermarkt API (free tier). May require caching or API upgrade.
- 🎯 **Summary Accuracy**: AI summaries may lack full context or introduce slight bias. Always verify important news with official sources.
- 📱 **UI/UX Enhancements**: Plan to improve responsiveness, add dark mode and polish the interface.

Despite these challenges, TransFolio offers a streamlined, full-stack solution for football fans to stay updated on player transfers.

---

## 📚 References

- [Gemini API – Google AI for Developers](https://ai.google.dev/api/all-methods)
- [AI Summarization | Google Cloud](https://cloud.google.com/use-cases/ai-summarization)
- [Spring Boot Task Scheduling](https://spring.io/guides/gs/scheduling-tasks/)
- [What is Java Spring Boot? – Azure](https://azure.microsoft.com/en-us/resources/cloud-computing-dictionary/what-is-java-spring-boot)
- [PostgreSQL – Official Site](https://www.postgresql.org/)
- [Render PostgreSQL Docs](https://render.com/docs/postgresql)
- [React Official Site](https://legacy.reactjs.org/)
- [Vercel – Deploy Fast Web Apps](https://vercel.com/)

---

## 📜 License

**This is a personal project** – I created because I’m passionate about football ⚽️. Feel free to make it your own or improve it in any way you like!

---
