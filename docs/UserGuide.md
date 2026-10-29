---
layout: page
title: User Guide
---

TutorConnect is a **desktop application for tutors to manage student and guardian contacts**, optimized for use through a Command Line Interface (CLI) while retaining the benefits of a Graphical User Interface (GUI).

* Table of Contents
{:toc}

--------------------------------------------------------------------------------------------------------------------

## Quick start

1. Ensure that Java `25` or later is installed on your computer.<br>
   **Mac users:** Ensure you have the precise JDK version prescribed [here](https://se-education.org/guides/tutorials/javaInstallationMac.html).

1. Download the latest `.jar` file from [here](https://github.com/AY2627S1-CS2103T-F14-1/tp/releases).

1. Copy the file to the folder you want to use as the _home folder_ for TutorConnect.

1. Open a terminal, `cd` to the folder containing the JAR file, and run `java -jar TutorConnect-v1.5.jar` (use the name of the file you downloaded).<br>
   A TutorConnect window should appear in a few seconds with an empty contact list.

   <div markdown="span" class="alert alert-warning">:exclamation: **Caution:**
   Launch the app from a terminal as shown above, as double-clicking the JAR file may fail on some systems. Use a folder you can write to: TutorConnect saves its data next to the JAR file, so it may fail to start or to save from a write-protected folder.
   </div>

1. Type a command in the command box and press Enter to execute it. For example, type **`help`** and press Enter to open the help window.<br>
   Some example commands you can try:

   * `add-student n/Alex Tan p/91234567` : Adds student `S1`.

   * `add-guardian n/Amelia Tan p/92345678` : Adds guardian `G1`.

   * `link s/S1 g/G1` : Records that guardian `G1` is linked to student `S1`.

   * `list` : Lists TutorConnect contacts and their relationships.

   * `exit` : Exits the app.

1. Refer to the [Features](#features) section below for details of each command.

--------------------------------------------------------------------------------------------------------------------

## Features

<div markdown="block" class="alert alert-info">

**:information_source: Notes about the command format:**<br>

* Words in `UPPER_CASE` are the parameters to be supplied by the user.<br>
  For example, in `add-student n/NAME`, replace `NAME` with a value such as `Alex Tan`.

* Items in square brackets are optional.<br>
  For example, `n/NAME [t/TAG]` can be used as `n/John Doe t/friend` or as `n/John Doe`.

* Items followed by `…`​ can appear zero or more times.<br>
  For example, `[t/TAG]…​` may be omitted, or written as `t/friend` or `t/friend t/family`.

* Parameters can be in any order.<br>
  For example, if the command specifies `n/NAME p/PHONE_NUMBER`, `p/PHONE_NUMBER n/NAME` is also acceptable.

* Extraneous parameters for commands that take no parameters, such as `help`, `list`, `exit`, and `clear`, are ignored.<br>
  For example, `help 123` is interpreted as `help`.

* If you are using a PDF version of this document, be careful when copying and pasting commands that span multiple lines as space characters surrounding line-breaks may be omitted when copied over to the application.
</div>

### Viewing help: `help`

Shows a message explaining how to access the help page.

![help message](images/helpMessage.png)

Format: `help`


### Adding a student: `add-student`

Adds a student and assigns the next permanent student ID (`S1`, `S2`, …).

Format: `add-student n/NAME p/PHONE_NUMBER`

* `NAME` can contain letters, digits and spaces, up to 100 characters.
* `PHONE_NUMBER` can contain digits only, between 3 and 15 digits long.

Example: `add-student n/Alex Tan p/91234567`

### Adding a guardian: `add-guardian`

Adds a guardian and assigns the next permanent guardian ID (`G1`, `G2`, …).

Format: `add-guardian n/NAME p/PHONE_NUMBER`

* `NAME` and `PHONE_NUMBER` follow the same rules as for `add-student`.

Example: `add-guardian n/Amelia Tan p/92345678`

### Linking a guardian to a student: `link`

Links existing contacts using their IDs. A guardian can be linked to more than one student.

* Like `add-student`, `add-guardian`, `edit` and `delete`, a successful `link` clears any filter left by a previous `find`, so the contact list shows everyone again.

Format: `link s/STUDENT_ID g/GUARDIAN_ID`

Example: `link s/S1 g/G1`

### Listing contacts: `list`

Shows all contacts, clearing any filter from a previous `find`. The result box lists every student and guardian with their ID, name, phone number, and linked contacts.

Format: `list`

* Students are shown first, then guardians, each in ID order (`S2` comes before `S10`).
* Each student line shows their linked guardians, and each guardian line shows their linked students. `none` means no links have been recorded yet.
* If there are no students or guardians, TutorConnect shows `No students or guardians yet.`

Example: after adding students `S1` and `S2` and guardian `G1`, then running `link s/S1 g/G1` and `link s/S2 g/G1`, `list` shows:

```
Listed 2 students and 1 guardian.
Students:
S1 Alex Tan, 91234567 (guardians: G1)
S2 Ben Lim, 93456789 (guardians: G1)
Guardians:
G1 Amelia Tan, 92345678 (students: S1, S2)
```

### Editing a contact: `edit`

Format: `edit ID [n/NAME] [p/PHONE] [e/EMAIL] [a/ADDRESS]`

* Use the student or guardian ID shown in the contact list, such as `S1` or `G2`.
* Provide at least one field. Existing values are replaced; the ID and guardian links stay unchanged.
* Tags cannot be edited for TutorConnect contacts because they contain the contact ID.
* The legacy `edit INDEX` form targets a position in the current filtered list. Since TutorConnect displays IDs instead of list positions, use the ID form for TutorConnect contacts.

Examples:
* `edit S1 p/91234567` changes student S1's phone number.
* `edit G2 n/Amelia Tan p/92345678` changes guardian G2's name and phone number.

### Finding contacts by name: `find`

Finds students and guardians whose names contain any of the given keywords. The result box lists each match with their ID, name, phone number, and linked contacts, in the same format as `list`.

Format: `find KEYWORD [MORE_KEYWORDS]`

* The search is case-insensitive; for example, `tan` matches `Tan`.
* Keyword order does not matter; for example, `Tan Alex` matches `Alex Tan`.
* The search considers only names.
* Only full words match; for example, `Ale` does not match `Alex`.
* Contacts matching at least one keyword are returned (an `OR` search); for example, `Ben Chloe` returns `Ben Lim` and `Chloe Ng`.
* If nothing matches, TutorConnect shows `No students or guardians match your search.`
* Use `list` to show all contacts again.

Example: with the contacts from the `list` example above, `find tan` shows:

```
Found 1 student and 1 guardian.
Students:
S1 Alex Tan, 91234567 (guardians: G1)
Guardians:
G1 Amelia Tan, 92345678 (students: S1, S2)
```

### Deleting a contact: `delete`

Deletes the specified student or guardian from TutorConnect.

Format: `delete ID` or `delete INDEX`

* `delete S1` deletes student `S1`; `delete G1` deletes guardian `G1`.
  Any guardian-student relationships involving that contact are removed automatically.
* Contact IDs are never reused.

* The legacy `delete INDEX` form targets a position in the current filtered list. Since TutorConnect displays IDs instead of list positions, use the ID form for TutorConnect contacts.

Example: `delete S1` deletes student S1 and its guardian links.

### Clearing all entries: `clear`

Clears all students, guardians and links from TutorConnect. This takes effect immediately and cannot be undone. Student and guardian IDs are not reused afterwards.

Format: `clear`

### Exiting the program: `exit`

Exits the program.

Format: `exit`

### Saving the data

TutorConnect automatically saves data after every command. You do not need to save manually.

### Editing the data file

TutorConnect data is saved automatically as a JSON file `[JAR file location]/data/tutorconnect.json`.
Advanced users are welcome to update data directly by editing that data file.

<div markdown="span" class="alert alert-warning">:exclamation: **Caution:**
If your changes make the data file invalid, TutorConnect starts with an empty workspace at the next run. The invalid file remains on disk until you run a command (TutorConnect saves after every command). Still, we recommend backing up the file before editing it.<br>
Furthermore, certain edits can cause TutorConnect to behave unexpectedly (for example, if a value is outside the acceptable range). Therefore, edit the data file only if you are confident that you can update it correctly.
</div>

--------------------------------------------------------------------------------------------------------------------

## FAQ

**Q**: How do I transfer my data to another computer?<br>
**A**: Install the app on the other computer and overwrite the data file it creates with the data file from your previous TutorConnect home folder.

--------------------------------------------------------------------------------------------------------------------

## Known issues

1. **When using multiple screens**, if you move the application to a secondary screen, and later switch to using only the primary screen, the GUI will open off-screen. The remedy is to delete the `preferences.json` file created by the application before running the application again.
2. **If you minimize the Help Window** and then run the `help` command (or use the `Help` menu, or the keyboard shortcut `F1`) again, the original Help Window will remain minimized, and no new Help Window will appear. The remedy is to manually restore the minimized Help Window.
3. **On macOS, if the app is in full-screen mode**, secondary windows such as the Help Window may not appear correctly. Leave full-screen mode before using `help`.
4. **Names and phone numbers are checked loosely.** Names accept only letters, digits and spaces (up to 100 characters), and phone numbers accept only digits (3 to 15 of them). Symbols such as `'`, `-`, `/` and `+` are rejected for now.

--------------------------------------------------------------------------------------------------------------------

## Command summary

Action | Format, Examples
--------|------------------
**Add student** | `add-student n/NAME p/PHONE_NUMBER`<br> e.g., `add-student n/Alex Tan p/91234567`
**Add guardian** | `add-guardian n/NAME p/PHONE_NUMBER`<br> e.g., `add-guardian n/Amelia Tan p/92345678`
**Clear** | `clear`
**Delete** | `delete ID`<br> e.g., `delete S1`
**Edit** | `edit ID [n/NAME] [p/PHONE_NUMBER] [e/EMAIL] [a/ADDRESS]`<br> e.g., `edit S1 n/James Lee`
**Exit** | `exit`
**Find** | `find KEYWORD [MORE_KEYWORDS]`<br> e.g., `find alex amelia`
**List** | `list`
**Link** | `link s/STUDENT_ID g/GUARDIAN_ID`<br> e.g., `link s/S1 g/G1`
**Help** | `help`
