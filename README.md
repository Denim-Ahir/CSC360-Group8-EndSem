# ImageLite – Image Compressor

A Java-based desktop application developed as part of the CSC360: Computer Graphics and Digital Image Processing course.

ImageLite aims to reduce image file sizes while maintaining acceptable visual quality. It provides a simple graphical interface through which users can load images, adjust compression quality, resize images, and save the compressed output.

## Project Objectives

- Reduce image file sizes.
- Allow users to control image compression quality.
- Support image resizing while maintaining aspect ratio.
- Display original and compressed image information.
- Provide a simple and user-friendly desktop interface.

## Planned Features

- Load JPG, JPEG, and PNG images.
- Preview the selected original image.
- Adjust JPEG compression quality.
- Resize images by specifying dimensions.
- Maintain aspect ratio during resizing.
- Display original and compressed file sizes.
- Calculate percentage size reduction.
- Save the compressed image to a selected location.

## Technologies Used

- **Programming Language:** Java
- **GUI Framework:** Java Swing
- **Build Tool:** Apache Maven
- **Image Processing:** Java AWT and ImageIO
- **Version Control:** Git and GitHub

## Project Structure

```text
CSC360-Group8-EndSem/
├── docs/
├── screenshots/
├── src/
│   ├── main/
│   │   ├── java/com/csc360/
│   │   │   ├── compression/
│   │   │   ├── gui/
│   │   │   ├── imageprocessing/
│   │   │   └── utils/
│   │   └── resources/
│   └── test/
│       └── java/com/csc360/
├── .gitignore
├── pom.xml
└── README.md
```

## How to Run

### Requirements

- Java JDK 21 or later
- Apache Maven 3.9 or later

### Build the Project

Clone the repository:

```bash
git clone https://github.com/Denim-Ahir/CSC360-Group8-EndSem.git
```

Navigate to the project directory:

```bash
cd CSC360-Group8-EndSem
```

Build the application:

```bash
mvn clean package
```

Run the application:

```bash
java -cp target/classes com.csc360.Main
```

## Development Status

The project is currently under development. Features and documentation will be updated as implementation progresses.

## Academic Information

**Course:** CSC360 – Computer Graphics and Digital Image Processing

**Project:** ImageLite – Image Compressor

**Repository:** [GitHub Repository](https://github.com/Denim-Ahir/CSC360-Group8-EndSem)# CSC360-Group8-EndSem