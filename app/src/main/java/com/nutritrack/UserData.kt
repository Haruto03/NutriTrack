package com.nutritrack
import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader

data class UserData(
    val phoneNumber: String,
    val userId: String,
    val sex: String
)

fun readCsvFromAssets(context: Context, fileName: String): List<String> {
    val lines = mutableListOf<String>()
    try {
        context.assets.open(fileName).use { inputStream ->
            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                var line: String? = reader.readLine()
                while (line != null) {
                    lines.add(line)
                    line = reader.readLine()
                }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return lines
}


fun parseCsv(lines: List<String>): List<UserData> {
    if (lines.isEmpty()) return emptyList()
    val dataLines = lines.drop(1)
    return dataLines.mapNotNull { line ->
        val columns = line.split(",")
        if (columns.size < 3) null
        else UserData(
            phoneNumber = columns[0],
            userId = columns[1],
            sex = columns[2]
        )
    }
}

fun extractFoodQualityScore(
    userId: String,
    phoneNumber: String,
    csvLines: List<String>
): Float? {
    val dataLines = csvLines.drop(1)
    for (line in dataLines) {
        val tokens = line.split(",")
        if (tokens.size >= 7 &&
            tokens[1] == userId &&
            tokens[0] == phoneNumber
        ) {
            val sex = tokens[2]
            return if (sex.equals("Male", ignoreCase = true)) {
                tokens[3].toFloatOrNull()
            } else {
                tokens[4].toFloatOrNull()
            }
        }
    }
    return null
}

fun extractCategoryScores(userId: String, phoneNumber: String, csvLines: List<String>): Map<String, Float>? {
    val categories = listOf(
        "Discretionary",
        "Vegetables",
        "Fruit",
        "Grains and cereals",
        "Wholegrains",
        "Meat and alternatives",
        "Dairy and alternatives",
        "Sodium",
        "Alcohol",
        "Water",
        "Sugar",
        "Saturated Fat",
        "Unsaturated Fat"
    )


    val maleIndexes = listOf(5, 8, 19, 29, 33, 36, 40, 43, 46, 49, 54, 57, 60)
    val femaleIndexes = listOf(6, 9, 20, 30, 34, 37, 41, 44, 47,50, 55, 58, 61)

    for (line in csvLines.drop(1)) {
        val cols = line.split(",")
        if (cols.size < 63) continue

        if (cols[0].trim() == phoneNumber && cols[1].trim() == userId) {
            val sex = cols[2].trim()
            val scores = mutableMapOf<String, Float>()
            val indexes = if (sex.equals("Male", ignoreCase = true)) maleIndexes else femaleIndexes

            for (i in categories.indices) {
                val score = cols[indexes[i]].toFloatOrNull() ?: 0f
                scores[categories[i]] = score
            }
            return scores.toMap()
        }
    }


    return null
}