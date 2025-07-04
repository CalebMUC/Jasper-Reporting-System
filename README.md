# 🧾 Jasper Reporting System (POS)

This is a full-stack Point-of-Sale (POS) reporting system that uses **JasperReports embedded in Java (Spring Boot)** for generating PDF reports, a **.NET Web API** as middleware, and a **React frontend** for downloading reports. The backend connects to **PostgreSQL** and is Dockerized for flexibility across cloud or client machines.

---

## ⚙️ Tech Stack

| Layer      | Technology                     |
|------------|--------------------------------|
| Reports    | Java (Spring Boot) + JasperReports |
| API        | .NET 8 Web API (C#)            |
| Frontend   | React.js                       |
| Database   | PostgreSQL                     |
| Orchestration | Docker Compose              |

---

## 🚀 Features

- 📄 Generate dynamic PDF reports using `.jrxml` files
- 🧬 Embedded JasperReports engine (no server needed)
- 🌐 .NET middleware to abstract report logic
- ⚡ React UI to trigger download
- 🐳 Docker Compose for full environment setup
- 🌍 Can run both online (Render + NeonDB) or offline (client machine)

---

## 📁 Project Structure

