# citation-manager
A full stack application for managing and organizing citations which lets users create accounts, sign in and create their own reference lists. Consists of a React (Typescript) frontend and a Java Spring backend which is backed by a PostgreSQL database, using JWT auth for users.
<br><br>The citation editor currently supports the following reference styles:
<ul>
    <li>American Psychological Association</li>
    <li>Vancouver</li>
    <li>American Chemical Society</li>
    <li>IEEE</li>
    <li>Modern Language Association</li>
    <li>Chicago 17th Edition</li>
</ul>

## Screenshot
<img width="913" height="413" alt="citation_manager_1" src="https://github.com/user-attachments/assets/e4bdf097-83a9-4ee7-adf0-3261c0cbd8b5" />

## Tech stack
<b>Frontend</b>: React (Typescript + Vite)<br>
<b>Backend</b>: Java Spring + PostgreSQL DB

## Authentication
Authentication is managed through the Spring security configuration which uses JWT based authentication with access token + refresh token pairs.

## Citation styles (.csl)
Reference style formatting uses CSL files that are sourced from https://github.com/citation-style-language/styles which falls under the Creative Commons Attribution-ShareAlike 3.0 Unported license. See: https://citationstyles.org/.
