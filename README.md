# Book Organizer

[![CI](https://github.com/ttocatta/book-organizer/actions/workflows/ci.yml/badge.svg)](https://github.com/ttocatta/book-organizer/actions)
[![Gitleaks](https://github.com/ttocatta/book-organizer/actions/workflows/gitleaks.yml/badge.svg)](https://github.com/ttocatta/book-organizer/actions)
[![License](https://img.shields.io/github/license/ttocatta/book-organizer.svg)](LICENSE)
[![Last Commit](https://img.shields.io/github/last-commit/ttocatta/book-organizer.svg)](https://github.com/ttocatta/book-organizer/commits/main)

A small Java project that helps you organize and track books. Intended as a clean, resume-friendly sample project showing build/test automation and security checks.

Quick start (local)
Prerequisites:
- Java 17+
- Maven
- Git (for full history scans if using gitleaks locally)

Clone, build, and run:
```bash
git clone https://github.com/ttocatta/book-organizer.git
cd book-organizer
mvn clean package
# run the app (example)
java -jar target/book-organizer.jar
```

Run tests:
```bash
mvn test
```

Security & secret scanning
- This repository runs Gitleaks on pushes and PRs to catch accidental secrets.
- To run the same scan locally (no binary in repo):
  - Docker (recommended):
    docker run --rm -v "${PWD}:/repo" zricethezav/gitleaks:latest detect /repo --report-format json --report-path /repo/gitleaks-report.json
  - Or use a local gitleaks executable (do NOT commit the exe):
    ./gitleaks detect --source . --report-format json --report-path ./gitleaks-report.json

If Gitleaks finds a secret:
1. Rotate the exposed credential immediately with the provider.
2. Remove the secret from git history (use git-filter-repo or BFG) and force-push the cleaned repo.
3. Re-run scans and confirm remediation.

Repository hygiene
- License: MIT (see LICENSE)
- If you plan to contribute, read CONTRIBUTING.md and CODE_OF_CONDUCT.md
- For security issues: see SECURITY.md

Badges and status
- CI shows build & tests status.
- Gitleaks workflow prevents secret leaks from being merged.

Contact / Resume note
- This repo is maintained as a resume reference demonstrating secure CI and best-practice repository hygiene.



## Function:

- Smart Sorting (Merge Sort Algorithm)
      Automatically organizes your book list based on Title, Author, Genre, or the year it was published.
      Allows you to toggle between Ascending (A-Z) and Descending (Z-A) order instantly.
      
- Instant Search Engine
      Features a "Search-as-you-type" function.
      Scans through titles, authors, and categories simultaneously to find the exact book you are looking for in a large collection.

- Digital Filing Cabinet (CSV Storage)
      Save: Permanently exports your library data to a spreadsheet file (book.csv) so you don't lose your progress.
      Load: Quickly imports existing book lists back into the program upon startup.

- Book Detail Management
      Full Profiles: Stores rich information for every book, including the cover image, author details, and the date you added it to your collection.
      Document Integration: Allows you to link a digital copy (like a PDF or Ebook file) directly to the entry, so you can open and read the book with one click.

- Dynamic Visual Views
      Simple View: A clean, spreadsheet-style list for quick scanning.
      Big Picture View: A visual gallery mode that showcases book cover thumbnails (similar to a digital bookshelf).

- User-Friendly Controls
      Pagination: Breaks large libraries into manageable pages (25 books per page) to prevent the screen from feeling cluttered.
      Interactive Menu: Includes a built-in "How to Use" guide with step-by-step tutorials to help new users navigate the system.


## Resources Used

Tech
- Java 17
- Maven
- Unit tests with JUnit
- CI: GitHub Actions
- Automated secret scanning: Gitleaks (GitHub Actions)
  
## Libraries

The following dependencies are included in this build:

Apache PDFBox: `pdfbox-app-3.0.6.jar`
Apache POI: `poi-ooxml-full-5.2.3.jar`
Apache Commons Collections: `commons-collections4-4.4.jar`
Apache Commons IO: `commons-io-2.11.0.jar`
Log4j API: `log4j-api-2.18.0.jar`
XMLBeans: `xmlbeans-5.1.1.jar`

(in case you don't have them you must install it, or clone my uploaded `lib` containting them)

## How to Run
1. Clone the repository `git clone https://github.com/ttocatta/book-organizer`
2. Open the project in IntelliJ IDEA (or any Java IDE)
3. Look for `src` folder
4. Ensure the required libraries are added to the project classpath
5. Locate the `BookOrganizerApp` class
6. Run the program
