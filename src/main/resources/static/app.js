const API_BASE_URL = 'http://localhost:8080/api/notes';

// --- View Management Functions ---
const showScreen = (id) => {
    document.querySelectorAll('.screen').forEach(screen => {
        screen.classList.add('hidden');
        screen.classList.remove('active');
    });
    const targetScreen = document.getElementById(id);
    targetScreen.classList.remove('hidden');
    targetScreen.classList.add('active');
};

// --- Login Page Logic ---
document.getElementById('login-form').addEventListener('submit', (e) => {
    e.preventDefault();
    const studentCode = document.getElementById('student-code').value;
    const password = document.getElementById('password').value;
    const loginMessage = document.getElementById('login-message');

    // NOTE: This is a DUMMY login check!
    // In a real app, you would POST to /api/login and check the response status/token.
    if (studentCode === 'BWU/BTA/22/177' && password === 'nopass') {
        loginMessage.textContent = 'Login successful!';
        loginMessage.style.color = 'green';
        setTimeout(() => {
            showScreen('main-page');
            populateSearchDropdowns(); // Load data for the next page
        }, 500);
    } else {
        loginMessage.textContent = 'Invalid Student Code or Password.';
        loginMessage.style.color = 'red';
    }
});


// --- Main Page (Search) Logic ---
const departmentSelect = document.getElementById('department-select');
const courseSelect = document.getElementById('course-select');
const semesterSelect = document.getElementById('semester-select');

// Step 1: Populate all unique options (Dummy data mirroring database setup)
const populateSearchDropdowns = () => {
    // In a real app, you'd fetch /api/notes/departments, /api/notes/courses, etc.
    const sampleData = {
        departments: ["CSE-AIML", "CSE-DS", "DIP-CSE", "ECE", "MECH"],
        courses: {
            "CSE-AIML": ["AI", "OS", "DBMS","MACHINE LEARNING","NETWORKING"],
            "CSE-DS": ["AI", "DS", "DBMS","MACHINE LEARNING","NETWORKING"],
            "DIP-CSE": ["AI", "OOS", "DBMS","NETWORKING"],
            "ECE": ["Signals", "VLSI"],
            "MECH": ["MACHINE-DESIGN","Thermodynamics"]
        },
        semesters: ["SEM 1", "SEM 2", "SEM 3", "SEM 4", "SEM 5", "SEM 6", "SEM 7", "SEM 8"],
    };

    // Populate Department
    departmentSelect.innerHTML = '<option value="" disabled selected>Select Department</option>';
    sampleData.departments.forEach(dept => {
        const option = new Option(dept, dept);
        departmentSelect.add(option);
    });

    // Populate Semester
    semesterSelect.innerHTML = '<option value="" disabled selected>Select Semester</option>';
    sampleData.semesters.forEach(sem => {
        const option = new Option(sem, sem);
        semesterSelect.add(option);
    });

    // Handle Course population based on Department selection
    departmentSelect.addEventListener('change', () => {
        const selectedDept = departmentSelect.value;
        courseSelect.innerHTML = '<option value="" disabled selected>Select Course</option>';
        if (selectedDept && sampleData.courses[selectedDept]) {
            sampleData.courses[selectedDept].forEach(course => {
                const option = new Option(course, course);
                courseSelect.add(option);
            });
        }
        // Enable Course selection only after Department is chosen
        courseSelect.disabled = !selectedDept;
    });

    // Initially disable course selection
    courseSelect.disabled = true;
};

// Step 2: Handle Search Form submission
document.getElementById('search-form').addEventListener('submit', async (e) => {
    e.preventDefault();

    const department = departmentSelect.value;
    const courseName = courseSelect.value;
    const semester = semesterSelect.value;

    if (!department || !courseName || !semester) {
        alert("Please select Department, Course, and Semester.");
        return;
    }

    // Construct the search API URL
    const searchUrl = `${API_BASE_URL}/search?department=${department}&courseName=${courseName}&semester=${semester}`;

    try {
        const response = await fetch(searchUrl);
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        const notes = await response.json();
        renderNotesVault(notes); // Pass results to the Notes Vault page
        showScreen('notes-vault-page');

    } catch (error) {
        console.error('Error fetching notes:', error);
        alert('Failed to load notes. Check console/backend server.');
    }
});


// --- Notes Vault Logic ---
const notesListDiv = document.getElementById('notes-list');
const downloadAllButton = document.getElementById('download-all-button');
const backButton = document.getElementById('back-button');

const renderNotesVault = (notes) => {
    notesListDiv.innerHTML = ''; // Clear previous results

    if (notes.length === 0) {
        notesListDiv.innerHTML = '<p>No notes found for the selected criteria.</p>';
        downloadAllButton.classList.add('hidden');
        return;
    }

    const noteIds = [];

    notes.forEach(note => {
        noteIds.push(note.id); // Collect IDs for the 'Download All' feature

        const item = document.createElement('div');
        item.classList.add('note-item');
        item.innerHTML = `
            <span>${note.moduleName} - ${note.courseName}</span>
            <button onclick="downloadSingleNote(${note.id})">PDF</button>
        `;
        notesListDiv.appendChild(item);
    });

    // Update 'Download All' button state and click handler
    downloadAllButton.classList.remove('hidden');
    // Set a data attribute to store the IDs for the download call
    downloadAllButton.setAttribute('data-note-ids', noteIds.join(','));
};

const downloadSingleNote = (noteId) => {
    // This triggers the file download from the Spring Boot endpoint
    window.open(`${API_BASE_URL}/download/${noteId}`, '_blank');
};

downloadAllButton.addEventListener('click', () => {
    const ids = downloadAllButton.getAttribute('data-note-ids');
    if (ids) {
        // This triggers the ZIP download from the Spring Boot endpoint
        window.open(`${API_BASE_URL}/download/all?ids=${ids}`, '_blank');
    }
});

backButton.addEventListener('click', () => {
    showScreen('main-page');
});

// --- Initialization ---
document.addEventListener('DOMContentLoaded', () => {
    showScreen('login-page');
});