-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1:3306
-- Generation Time: May 21, 2026 at 04:34 PM
-- Server version: 9.1.0
-- PHP Version: 8.3.14

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `onlineexam`
--

-- --------------------------------------------------------

--
-- Table structure for table `course`
--

DROP TABLE IF EXISTS `course`;
CREATE TABLE IF NOT EXISTS `course` (
  `course_id` int NOT NULL AUTO_INCREMENT,
  `course_code` varchar(20) NOT NULL,
  `course_name` varchar(50) NOT NULL,
  `credit_hour` int NOT NULL,
  PRIMARY KEY (`course_id`)
) ENGINE=MyISAM AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `course`
--

INSERT INTO `course` (`course_id`, `course_code`, `course_name`, `credit_hour`) VALUES
(1, 'CSC2010', 'JAVA PROGRAMMING', 5),
(2, 'CSC2011', 'ALGORIZM ANALYSIS', 5),
(5, 'CSC2012', 'OS', 5);

-- --------------------------------------------------------

--
-- Table structure for table `exams`
--

DROP TABLE IF EXISTS `exams`;
CREATE TABLE IF NOT EXISTS `exams` (
  `exam_id` int NOT NULL AUTO_INCREMENT,
  `course_id` int NOT NULL,
  `instructor_id` int NOT NULL,
  `title` varchar(100) NOT NULL,
  `time_limit` int NOT NULL,
  `total_marks` int NOT NULL,
  `exam_date` datetime DEFAULT NULL,
  `exam_code` varchar(50) NOT NULL,
  PRIMARY KEY (`exam_id`),
  KEY `fk_exam_course` (`course_id`),
  KEY `fk_exam_instructor` (`instructor_id`)
) ENGINE=MyISAM AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `exams`
--

INSERT INTO `exams` (`exam_id`, `course_id`, `instructor_id`, `title`, `time_limit`, `total_marks`, `exam_date`, `exam_code`) VALUES
(22, 1, 2, 'Final', 1, 3, '2026-05-22 00:00:00', '123'),
(21, 5, 3, 'Final', 60, 20, '2026-05-29 00:00:00', 'OOO'),
(20, 2, 1, 'MID', 1, 3, '2026-05-30 00:00:00', 'JJJ');

-- --------------------------------------------------------

--
-- Table structure for table `instructors`
--

DROP TABLE IF EXISTS `instructors`;
CREATE TABLE IF NOT EXISTS `instructors` (
  `instructor_id` int NOT NULL AUTO_INCREMENT,
  `first_name` varchar(50) NOT NULL,
  `last_name` varchar(50) NOT NULL,
  `email` varchar(100) DEFAULT NULL,
  `department` varchar(100) DEFAULT NULL,
  `office_number` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`instructor_id`),
  UNIQUE KEY `email` (`email`)
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `instructors`
--

INSERT INTO `instructors` (`instructor_id`, `first_name`, `last_name`, `email`, `department`, `office_number`) VALUES
(1, 'Eskedar', 'Worku', 'eskedar@gmail.com', 'Computer Science', '201'),
(2, 'Abel', 'Mulu', 'abel@gmail.com', 'Computer Science', '201'),
(3, 'Alemu', 'Getu', 'alemu@gmail.com', 'Computer Science', '202');

-- --------------------------------------------------------

--
-- Table structure for table `instructor_course`
--

DROP TABLE IF EXISTS `instructor_course`;
CREATE TABLE IF NOT EXISTS `instructor_course` (
  `id` int NOT NULL AUTO_INCREMENT,
  `instructor_id` int NOT NULL,
  `course_id` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `instructor_id` (`instructor_id`,`course_id`),
  KEY `course_id` (`course_id`)
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `instructor_course`
--

INSERT INTO `instructor_course` (`id`, `instructor_id`, `course_id`) VALUES
(1, 1, 2),
(2, 2, 1),
(3, 3, 5);

-- --------------------------------------------------------

--
-- Table structure for table `questions`
--

DROP TABLE IF EXISTS `questions`;
CREATE TABLE IF NOT EXISTS `questions` (
  `question_id` int NOT NULL AUTO_INCREMENT,
  `exam_id` int DEFAULT NULL,
  `question_text` text,
  `option_a` varchar(255) DEFAULT NULL,
  `option_b` varchar(255) DEFAULT NULL,
  `option_c` varchar(255) DEFAULT NULL,
  `option_d` varchar(255) DEFAULT NULL,
  `correct_answer` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `mark` float DEFAULT NULL,
  PRIMARY KEY (`question_id`),
  KEY `exam_id` (`exam_id`)
) ENGINE=MyISAM AUTO_INCREMENT=39 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `questions`
--

INSERT INTO `questions` (`question_id`, `exam_id`, `question_text`, `option_a`, `option_b`, `option_c`, `option_d`, `correct_answer`, `mark`) VALUES
(38, 22, 'rtdyuhi rtdyui', 'ertyuio', '4e5rtyu', 'ertyu', 'ertyu', '', 1.5),
(37, 22, 'werty sertyu drftgyhu', 'wertyui', 'setryf', 'rtyu', 'rtyui', 'A', 1.5),
(36, 21, 'ERTYUER SDRTYU SERTYU DRTFYUH', 'DFG', 'DFGDRTFH', 'TYUJ', 'RTYHU', 'A', 1.5),
(35, 21, 'ERTYUI ERTYU RTYU ERTYUI', 'DRFTGY', 'DXFGH', 'DFG', 'DFG', 'A', 1.5),
(34, 21, 'ERTY ERTYU ERTYU SERTYU RTYHUJ', 'RDFT', 'XDFG', 'DFG', 'DFG', 'A', 1.5),
(33, 20, 'ERTY DRTFY RTYU DTFYU DTYU', 'DRFTY', 'DFT', 'SDFG', 'DFG', 'A', 1.5),
(32, 20, 'ERTY DFTYU RTYU RTYUI', 'SEDR', 'DF', 'DFGH', 'DRFTGY', 'A', 1.5);

-- --------------------------------------------------------

--
-- Table structure for table `students`
--

DROP TABLE IF EXISTS `students`;
CREATE TABLE IF NOT EXISTS `students` (
  `student_id` int NOT NULL AUTO_INCREMENT,
  `full_name` varchar(60) NOT NULL,
  `department` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'CS',
  `year_level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '3rd',
  `semester` int NOT NULL,
  PRIMARY KEY (`student_id`)
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `students`
--

INSERT INTO `students` (`student_id`, `full_name`, `department`, `year_level`, `semester`) VALUES
(1, 'dagmawit mesfin', 'CS', '3rd', 2),
(2, 'alemu getu', 'CS', '3rd', 2),
(3, 'Amanuel Muluken', 'CS', '3rd', 2);

-- --------------------------------------------------------

--
-- Table structure for table `student_answers`
--

DROP TABLE IF EXISTS `student_answers`;
CREATE TABLE IF NOT EXISTS `student_answers` (
  `id` int NOT NULL AUTO_INCREMENT,
  `student_id` int DEFAULT NULL,
  `exam_id` int DEFAULT NULL,
  `question_id` int DEFAULT NULL,
  `answer` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=29 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `student_answers`
--

INSERT INTO `student_answers` (`id`, `student_id`, `exam_id`, `question_id`, `answer`) VALUES
(28, 1, 22, 37, 'C'),
(27, 1, 22, 38, 'A'),
(26, 2, 21, 34, 'C'),
(25, 2, 21, 35, 'A'),
(24, 2, 21, 36, 'A'),
(23, 1, 20, 32, 'C'),
(22, 1, 20, 33, 'A'),
(21, 1, 11, 20, 'B');

-- --------------------------------------------------------

--
-- Table structure for table `student_exams`
--

DROP TABLE IF EXISTS `student_exams`;
CREATE TABLE IF NOT EXISTS `student_exams` (
  `student_exam_id` int NOT NULL AUTO_INCREMENT,
  `student_id` int NOT NULL,
  `exam_id` int NOT NULL,
  `score` double DEFAULT '0',
  PRIMARY KEY (`student_exam_id`),
  KEY `fk_se_student` (`student_id`),
  KEY `fk_se_exam` (`exam_id`)
) ENGINE=MyISAM AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `student_exams`
--

INSERT INTO `student_exams` (`student_exam_id`, `student_id`, `exam_id`, `score`) VALUES
(18, 1, 22, 0),
(17, 2, 21, 3),
(16, 1, 20, 1.5),
(15, 1, 20, 0);

-- --------------------------------------------------------

--
-- Table structure for table `userstaff`
--

DROP TABLE IF EXISTS `userstaff`;
CREATE TABLE IF NOT EXISTS `userstaff` (
  `staff_id` int NOT NULL AUTO_INCREMENT,
  `instructor_id` int DEFAULT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role` enum('Instructor','Admin') DEFAULT 'Instructor',
  `status` enum('Active','Inactive') DEFAULT 'Active',
  PRIMARY KEY (`staff_id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `instructor_id` (`instructor_id`)
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `userstaff`
--

INSERT INTO `userstaff` (`staff_id`, `instructor_id`, `username`, `password`, `role`, `status`) VALUES
(1, 1, 'eskedar', '12345', 'Instructor', 'Active'),
(2, 2, 'abel', '12345', 'Instructor', 'Active'),
(3, 3, 'alemu', '12345', 'Instructor', 'Active');

-- --------------------------------------------------------

--
-- Table structure for table `users_student`
--

DROP TABLE IF EXISTS `users_student`;
CREATE TABLE IF NOT EXISTS `users_student` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `student_id` int NOT NULL,
  `status` enum('ACTIVE','INACTIVE') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  KEY `student_id` (`student_id`)
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `users_student`
--

INSERT INTO `users_student` (`id`, `username`, `password`, `student_id`, `status`) VALUES
(1, 'dagi', '123', 1, 'ACTIVE'),
(2, 'alemu', '123', 2, 'ACTIVE'),
(3, 'aman', '123', 3, 'ACTIVE');
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
