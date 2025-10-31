package com.extremesudoku.data

import com.extremesudoku.data.models.Sudoku

object TestData {
    
    // ============ EASY PUZZLES (30-35 boş hücre) ============
    val EASY_PUZZLE_1 = Sudoku(
        id = "test_easy_1",
        puzzle = "530070000600195000098000060800060003400803001700020006060000280000419005000080079",
        solution = "534678912672195348198342567859761423426853791713924856961537284287419635345286179",
        difficulty = "easy",
        category = "classic"
    )
    
    val EASY_PUZZLE_2 = Sudoku(
        id = "test_easy_2",
        puzzle = "200080300060070084030500209000105408000000000402706000301007040720030010004010003",
        solution = "245981376169273584837564219976125438518349762432786951351697248729438615684512397",
        difficulty = "easy",
        category = "classic"
    )
    
    val EASY_PUZZLE_3 = Sudoku(
        id = "test_easy_3",
        puzzle = "600120384008459072000008519000264901070080025902375000491600258258941097006000413",
        solution = "695127384318459672724368519583264901476981325902375846491632758258741697367598413",
        difficulty = "easy",
        category = "classic"
    )
    
    // ============ MEDIUM PUZZLES (40-45 boş hücre) ============
    val MEDIUM_PUZZLE_1 = Sudoku(
        id = "test_medium_1",
        puzzle = "000000907000420180000705026100904000050000040000507009920108000034059000507000000",
        solution = "346281957798423185251765326132974568659318742874567239926148753834659271517032684",
        difficulty = "medium",
        category = "classic"
    )
    
    val MEDIUM_PUZZLE_2 = Sudoku(
        id = "test_medium_2",
        puzzle = "020608000580009700000040000370000500600000004008000013000020000009800036000306090",
        solution = "124678395586239741793541268371964582652387914948152673835429157219875436467316829",
        difficulty = "medium",
        category = "classic"
    )
    
    val MEDIUM_PUZZLE_3 = Sudoku(
        id = "test_medium_3",
        puzzle = "000080000000706500607000004760400000000000270005000090050000006000000430200610000",
        solution = "431285967928716543657394184769431852183952276245867391514273698876549213392618745",
        difficulty = "medium",
        category = "classic"
    )
    
    // ============ HARD PUZZLES (50+ boş hücre) ============
    val HARD_PUZZLE_1 = Sudoku(
        id = "test_hard_1",
        puzzle = "003020600900305001001806400008102900700000008006708200002609500800203009005010300",
        solution = "483921657967345821251876493548132976729564138136798245372689514814253769695417382",
        difficulty = "hard",
        category = "classic"
    )
    
    val HARD_PUZZLE_2 = Sudoku(
        id = "test_hard_2",
        puzzle = "000000000000003085001020000000507000004000100090000000500000073002010000000040009",
        solution = "987654321246173985351928746128597634634812157795346821519286473472319568863745219",
        difficulty = "hard",
        category = "classic"
    )
    
    val HARD_PUZZLE_3 = Sudoku(
        id = "test_hard_3",
        puzzle = "200700000050000008000004070090000600000508000003000020040200000100000050000006003",
        solution = "234798615957361428681254379598473612762518943413692827846235791179824356325916784",
        difficulty = "hard",
        category = "classic"
    )
    
    // ============ EXPERT PUZZLES (55+ boş hücre, X-Sudoku) ============
    val EXPERT_PUZZLE_1 = Sudoku(
        id = "test_expert_1",
        puzzle = "800000000003600000070090200050007000000045700000100030001000068008500010090000400",
        solution = "812753649943682175675491283154237896369845721287169534521974368438526917796318452",
        difficulty = "expert",
        category = "extreme",
        isXSudoku = true
    )
    
    val EXPERT_PUZZLE_2 = Sudoku(
        id = "test_expert_2",
        puzzle = "000000000000003085001020000000507000004000100090000000500000073002010000000040009",
        solution = "987654321246173985351928746128597634634812157795346821519286473472319568863745219",
        difficulty = "expert",
        category = "extreme",
        isXSudoku = true
    )
    
    // Daily challenge
    val DAILY_CHALLENGE = Sudoku(
        id = "daily_${System.currentTimeMillis()}",
        puzzle = "200080300060070084030500209000105408000000000402706000301007040720030010004010003",
        solution = "245981376169273584837564219976125438518349762432786951351697248729438615684512397",
        difficulty = "medium",
        category = "daily"
    )
    
    // All test puzzles grouped by difficulty
    val EASY_PUZZLES = listOf(EASY_PUZZLE_1, EASY_PUZZLE_2, EASY_PUZZLE_3)
    val MEDIUM_PUZZLES = listOf(MEDIUM_PUZZLE_1, MEDIUM_PUZZLE_2, MEDIUM_PUZZLE_3)
    val HARD_PUZZLES = listOf(HARD_PUZZLE_1, HARD_PUZZLE_2, HARD_PUZZLE_3)
    val EXPERT_PUZZLES = listOf(EXPERT_PUZZLE_1, EXPERT_PUZZLE_2)
    
    val ALL_TEST_PUZZLES = EASY_PUZZLES + MEDIUM_PUZZLES + HARD_PUZZLES + EXPERT_PUZZLES + DAILY_CHALLENGE
}
