# citation-manager
A full stack application for managing and organizing citations which lets users create accounts, sign in and create their own reference lists. Consists of a React (Typescript) frontend and a Java Spring backend which is backed by a PostgreSQL database, using JWT auth for users.

## Tech stack
<b>Frontend</b>: React (Typescript + Vite)<br>
<b>Backend</b>: Java Spring + PostgreSQL DB

## Authentication
The backend uses JWT based authentication with an access token + refresh token pair.

## Citation styles (.csl)
Reference style formatting uses CSL files that are sourced from https://github.com/citation-style-language/styles.