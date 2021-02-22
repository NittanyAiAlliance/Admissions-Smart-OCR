TRAIN_DATA = [
    # https://psu.app.box.com/file/615034288932
    # HSTRAN-3 table-1.csv
    ("16-17 Warwick School District Grade: 10", [(0, 2, "EXTRA"), (3, 3, "EXTRA"), (4, 4, "EXTRA"), (5, 5, "EXTRA"), (6, 6, "EXTRA"), (7, 7, "EXTRA"), (8, 8, "EXTRA")]),
    ("Course Grade Credit", [(0, 0, "EXTRA"), (1, 1, "EXTRA"), (2, 2, "EXTRA")]),
    ("College Prep English 10 B 1.00", [(0, 23, "COURSE"), (24, 25, "GRADE"), (26, 30, "CREDIT")]),
    ("French III B+ 1.00", [(0, 10, "COURSE"), (11, 13, "GRADE"), (14, 18, "CREDIT")]),
    ("College Prep World History B+ 1.00", [(0, 26, "COURSE"), (27, 29, "GRADE"), (30, 34, "CREDIT")]),
    ("Honors Chemistry B+ 1.00", [(0, 16, "COURSE"), (17, 19, "GRADE"), (20, 24, "CREDIT")]),
    ("College Prep Algebra II A- 1.00", [(0, 23, "COURSE"), (24, 26, "GRADE"), (27, 31, "CREDIT")]),
    ("Engineering Drawing A 0.50", [(0, 19, "COURSE"), (20, 21, "GRADE"), (22, 26, "CREDIT")]),
    ("Health A+ 0.50", [(0, 6, "COURSE"), (7, 9, "GRADE"), (10, 14, "CREDIT")]),
    ("Team Sports A+ 0.50", [(0, 11, "COURSE"), (12, 14, "GRADE"), (15, 19, "CREDIT")]),
    ("16-17 Cumulative Credits: Credits: 6.50 13.00", [(0, 5, "EXTRA"), (6, 16, "EXTRA"), (17, 25, "EXTRA"), (26, 34, "EXTRA"), (35, 39, "EXTRA"), (40, 45, "EXTRA")]),
    ("Cum. Weighted GPA: 3.26666 Rank: 144 of 350", [(0, 4, "EXTRA"), (5, 13, "EXTRA"), (14, 18, "EXTRA"), (19, 26, "EXTRA"), (27, 32, "EXTRA"), (33, 36, "EXTRA"), (37, 39, "EXTRA"), (40, 43, "EXTRA")]),
    
    # https://psu.app.box.com/file/615025686483
    # HSTRAN-1 table-1.csv
    ("Course Weighted Grade Credit", [(0, 6, "EXTRA"), (7, 15, "EXTRA"), (16, 21, "EXTRA"), (22, 28, "EXTRA")]),
    ("Selective Phys Ed/Fall 0 C 0.5", [(0, 22, "COURSE"), (23, 24, "WEIGHT"), (25, 26, "GRADE"), (27, 30, "CREDIT")]),
    ("Algebra 2 0 B 1", [(0, 9, "COURSE"), (10, 11, "WEIGHT"), (12, 13, "GRADE"), (14, 15, "CREDIT")]),
    ("US History O B 1", [(0, 10, "COURSE"), (11, 12, "WEIGHT"), (13, 14, "GRADE"), (15, 16, "CREDIT")]),
    ("English 11 O B- 1", [(0, 10, "COURSE"), (11, 12, "WEIGHT"), (13, 15, "GRADE"), (16, 17, "CREDIT")]),
    ("Chemistry 1 O B- 1", [(0, 11, "COURSE"), (12, 13, "WEIGHT"), (14, 16, "GRADE"), (17, 18, "CREDIT")]),
    ("Psychology 0 B 1", [(0, 10, "COURSE"), (11, 12, "WEIGHT"), (13, 14, "GRADE"), (15, 16, "CREDIT")]),
    ("Wellness for Life 0 C 0.5", [(0, 17, "COURSE"), (18, 19, "WEIGHT"), (20, 21, "GRADE"), (22, 25, "CREDIT")]),
    ("Criminology 0 A+ 0.5", [(0, 11, "COURSE"), (12, 13, "WEIGHT"), (14, 16, "GRADE"), (17, 20, "CREDIT")]),



    # https://psu.app.box.com/file/615034315551
    # HSTRAN-4 table-2.csv
    ("Course FINAL Credit", [(0, 6, "EXTRA"), (7, 12, "EXTRA"), (13, 19, "EXTRA")]),
    ("Brit Lit 12 C+ 0.50", [(0, 11, "COURSE"), (12, 14, "GRADE"), (15, 19, "CREDIT")]),
    ("Cont Am Lit C+ 0.50", [(0, 11, "COURSE"), (12, 14, "GRADE"), (15, 19, "CREDIT")]),
    ("AP Comparative Govt C 1.00", [(0, 19, "COURSE"), (20, 21, "GRADE"), (22, 26, "CREDIT")]),
    ("Pre Calculus D 1.00", [(0, 12, "COURSE"), (13, 14, "GRADE"), (15, 19, "CREDIT")]),
    ("Physics B 1.15", [(0, 7, "COURSE"), (8, 9, "GRADE"), (10, 14, "CREDIT")]),
    ("Digital Electronics H B 1.00", [(0, 21, "COURSE"), (22, 23, "GRADE"), (24, 28, "CREDIT")]),
    ("Computer Integ Manufacturing H C+ 1.00", [(0, 30, "COURSE"), (31, 33, "GRADE"), (34, 38, "CREDIT")]),
    ("PE 12 C 0.15", [(0, 5, "COURSE"), (6, 7, "GRADE"), (8, 12, "CREDIT")]),
    ("PE 12 B+ 0.15", [(0, 5, "COURSE"), (6, 8, "GRADE"), (9, 13, "CREDIT")]),
    # HSTRAN-4 table-4.csv
    ("America in History 3 H B+ 1.00", [(0, 22, "COURSE"), (23, 25, "GRADE"), (26, 30, "CREDIT")]),
    ("Intro to Engineering Des H (IED) B 1.00", [(0, 32, "COURSE"), (33, 34, "GRADE"), (35, 39, "CREDIT")]),
    ("Principles of Engineering H (POE) B 1.00", [(0, 33, "COURSE"), (34, 35, "GRADE"), (36, 40, "CREDIT")]),

]

def add_train_data(file):
    pass