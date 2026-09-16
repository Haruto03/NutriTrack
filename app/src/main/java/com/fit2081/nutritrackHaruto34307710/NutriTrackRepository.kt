// Path: NutriTrack/app/src/main/java/com/fit2081/nutritrack/NutriTrackRepository.kt
package com.fit2081.nutritrackHaruto34307710

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.fit2081.nutritrackHaruto34307710.BuildConfig
import com.fit2081.nutritrackHaruto34307710.data.AppDatabase
import com.fit2081.nutritrackHaruto34307710.data.dao.FoodIntakeDao
import com.fit2081.nutritrackHaruto34307710.data.dao.NutriCoachTipDao
import com.fit2081.nutritrackHaruto34307710.data.dao.PatientDao
import com.fit2081.nutritrackHaruto34307710.data.model.FoodIntake
import com.fit2081.nutritrackHaruto34307710.data.model.NutriCoachTip
import com.fit2081.nutritrackHaruto34307710.data.model.Patient
import com.fit2081.nutritrackHaruto34307710.network.FruityViceApiService
import com.fit2081.nutritrackHaruto34307710.viewModel.FruitDetails
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.GenerateContentResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import java.util.Locale

class NutriTrackRepository(private val application: Application) {
    private val patientDao: PatientDao
    private val foodIntakeDao: FoodIntakeDao
    private val nutriCoachTipDao: NutriCoachTipDao
    private val fruityViceApiService: FruityViceApiService

    private val generativeModel: GenerativeModel

    init {
        val database = AppDatabase.getDatabase(application)
        patientDao = database.patientDao()
        foodIntakeDao = database.foodIntakeDao()
        nutriCoachTipDao = database.nutriCoachTipDao()
        fruityViceApiService = FruityViceApiService.instance

        val apiKey = BuildConfig.API_KEY
        generativeModel = GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = apiKey
        )
    }

    private suspend fun readCsvFromAssetsToLines(context: Context, fileName: String): List<String> {
        return withContext(Dispatchers.IO) {
            val lines = mutableListOf<String>()
            try {
                context.assets.open(fileName).bufferedReader().useLines { sequence ->
                    sequence.forEach { lines.add(it) }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            lines
        }
    }

    suspend fun seedPatientDataFromCsv() {
        val lines = readCsvFromAssetsToLines(application.applicationContext, "CustomerData.csv")
        if (lines.size <= 1) {
            println("CSV file is empty or only contains header.")
            return
        }

        val patients = mutableListOf<Patient>()
        val header = lines[0].split(",")

        val phoneNumberIdx = header.indexOf("PhoneNumber")
        val userIdIdx = header.indexOf("User_ID")
        val sexIdx = header.indexOf("Sex")
        val heifaTotalMaleIdx = header.indexOf("HEIFAtotalscoreMale")
        val heifaTotalFemaleIdx = header.indexOf("HEIFAtotalscoreFemale")
        val discScoreMaleIdx = header.indexOf("DiscretionaryHEIFAscoreMale")
        val discScoreFemaleIdx = header.indexOf("DiscretionaryHEIFAscoreFemale")
        val discServeSizeIdx = header.indexOf("Discretionaryservesize")
        val vegScoreMaleIdx = header.indexOf("VegetablesHEIFAscoreMale")
        val vegScoreFemaleIdx = header.indexOf("VegetablesHEIFAscoreFemale")
        val vegLegServeSizeIdx = header.indexOf("Vegetableswithlegumesallocatedservesize")
        val legAllocVegIdx = header.indexOf("LegumesallocatedVegetables")
        val vegVarScoreIdx = header.indexOf("Vegetablesvariationsscore")
        val vegCrucIdx = header.indexOf("VegetablesCruciferous")
        val vegTuberIdx = header.indexOf("VegetablesTuberandbulb")
        val vegOtherIdx = header.indexOf("VegetablesOther")
        val legumesIdx = header.indexOf("Legumes")
        val vegGreenIdx = header.indexOf("VegetablesGreen")
        val vegRedOrangeIdx = header.indexOf("VegetablesRedandorange")
        val fruitScoreMaleIdx = header.indexOf("FruitHEIFAscoreMale")
        val fruitScoreFemaleIdx = header.indexOf("FruitHEIFAscoreFemale")
        val fruitServeSizeIdx = header.indexOf("Fruitservesize")
        val fruitVarScoreIdx = header.indexOf("Fruitvariationsscore")
        val fruitPomeIdx = header.indexOf("FruitPome")
        val fruitTropSubIdx = header.indexOf("FruitTropicalandsubtropical")
        val fruitBerryIdx = header.indexOf("FruitBerry")
        val fruitStoneIdx = header.indexOf("FruitStone")
        val fruitCitrusIdx = header.indexOf("FruitCitrus")
        val fruitOtherIdx = header.indexOf("FruitOther")
        val grainScoreMaleIdx = header.indexOf("GrainsandcerealsHEIFAscoreMale")
        val grainScoreFemaleIdx = header.indexOf("GrainsandcerealsHEIFAscoreFemale")
        val grainServeSizeIdx = header.indexOf("Grainsandcerealsservesize")
        val grainNonWholeIdx = header.indexOf("GrainsandcerealsNonwholegrains")
        val wholeGrainScoreMaleIdx = header.indexOf("WholegrainsHEIFAscoreMale")
        val wholeGrainScoreFemaleIdx = header.indexOf("WholegrainsHEIFAscoreFemale")
        val wholeGrainServeSizeIdx = header.indexOf("Wholegrainsservesize")
        val meatScoreMaleIdx = header.indexOf("MeatandalternativesHEIFAscoreMale")
        val meatScoreFemaleIdx = header.indexOf("MeatandalternativesHEIFAscoreFemale")
        val meatLegServeSizeIdx = header.indexOf("Meatandalternativeswithlegumesallocatedservesize")
        val legAllocMeatIdx = header.indexOf("LegumesallocatedMeatandalternatives")
        val dairyScoreMaleIdx = header.indexOf("DairyandalternativesHEIFAscoreMale")
        val dairyScoreFemaleIdx = header.indexOf("DairyandalternativesHEIFAscoreFemale")
        val dairyServeSizeIdx = header.indexOf("Dairyandalternativesservesize")
        val sodiumScoreMaleIdx = header.indexOf("SodiumHEIFAscoreMale")
        val sodiumScoreFemaleIdx = header.indexOf("SodiumHEIFAscoreFemale")
        val sodiumMgIdx = header.indexOf("Sodiummgmilligrams")
        val alcoholScoreMaleIdx = header.indexOf("AlcoholHEIFAscoreMale")
        val alcoholScoreFemaleIdx = header.indexOf("AlcoholHEIFAscoreFemale")
        val alcoholStdDrinksIdx = header.indexOf("Alcoholstandarddrinks")
        val waterScoreMaleIdx = header.indexOf("WaterHEIFAscoreMale")
        val waterScoreFemaleIdx = header.indexOf("WaterHEIFAscoreFemale")
        val waterIdx = header.indexOf("Water")
        val waterTotalMlIdx = header.indexOf("WaterTotalmL")
        val bevTotalMlIdx = header.indexOf("BeverageTotalmL")
        val sugarScoreMaleIdx = header.indexOf("SugarHEIFAscoreMale")
        val sugarScoreFemaleIdx = header.indexOf("SugarHEIFAscoreFemale")
        val sugarIdx = header.indexOf("Sugar")
        val satFatScoreMaleIdx = header.indexOf("SaturatedFatHEIFAscoreMale")
        val satFatScoreFemaleIdx = header.indexOf("SaturatedFatHEIFAscoreFemale")
        val satFatIdx = header.indexOf("SaturatedFat")
        val unsatFatScoreMaleIdx = header.indexOf("UnsaturatedFatHEIFAscoreMale")
        val unsatFatScoreFemaleIdx = header.indexOf("UnsaturatedFatHEIFAscoreFemale")
        val unsatFatServeSizeIdx = header.indexOf("UnsaturatedFatservesize")

        for (i in 1 until lines.size) {
            val tokens = lines[i].split(",")
            if (tokens.size == header.size) {
                try {
                    fun safeToFloat(index: Int): Float? = if (index != -1) tokens.getOrNull(index)?.trim()?.toFloatOrNull() else null
                    fun safeToString(index: Int, default: String = ""): String = if (index != -1) tokens.getOrNull(index)?.trim() ?: default else default

                    val patient = Patient(
                        phoneNumber = safeToString(phoneNumberIdx),
                        userId = safeToString(userIdIdx),
                        sex = safeToString(sexIdx),
                        name = null,
                        password = null,
                        heifaTotalScoreMale = safeToFloat(heifaTotalMaleIdx),
                        heifaTotalScoreFemale = safeToFloat(heifaTotalFemaleIdx),
                        discretionaryHEIFAScoreMale = safeToFloat(discScoreMaleIdx),
                        discretionaryHEIFAScoreFemale = safeToFloat(discScoreFemaleIdx),
                        discretionaryServeSize = safeToFloat(discServeSizeIdx),
                        vegetablesHEIFAScoreMale = safeToFloat(vegScoreMaleIdx),
                        vegetablesHEIFAScoreFemale = safeToFloat(vegScoreFemaleIdx),
                        vegetablesWithLegumesAllocatedServeSize = safeToFloat(vegLegServeSizeIdx),
                        legumesAllocatedVegetables = safeToFloat(legAllocVegIdx),
                        vegetablesVariationsScore = safeToFloat(vegVarScoreIdx),
                        vegetablesCruciferous = safeToFloat(vegCrucIdx),
                        vegetablesTuberAndBulb = safeToFloat(vegTuberIdx),
                        vegetablesOther = safeToFloat(vegOtherIdx),
                        legumes = safeToFloat(legumesIdx),
                        vegetablesGreen = safeToFloat(vegGreenIdx),
                        vegetablesRedAndOrange = safeToFloat(vegRedOrangeIdx),
                        fruitHEIFAScoreMale = safeToFloat(fruitScoreMaleIdx),
                        fruitHEIFAScoreFemale = safeToFloat(fruitScoreFemaleIdx),
                        fruitServeSize = safeToFloat(fruitServeSizeIdx),
                        fruitVariationsScore = safeToFloat(fruitVarScoreIdx),
                        fruitPome = safeToFloat(fruitPomeIdx),
                        fruitTropicalAndSubtropical = safeToFloat(fruitTropSubIdx),
                        fruitBerry = safeToFloat(fruitBerryIdx),
                        fruitStone = safeToFloat(fruitStoneIdx),
                        fruitCitrus = safeToFloat(fruitCitrusIdx),
                        fruitOther = safeToFloat(fruitOtherIdx),
                        grainsAndCerealsHEIFAScoreMale = safeToFloat(grainScoreMaleIdx),
                        grainsAndCerealsHEIFAScoreFemale = safeToFloat(grainScoreFemaleIdx),
                        grainsAndCerealsServeSize = safeToFloat(grainServeSizeIdx),
                        grainsAndCerealsNonWholegrains = safeToFloat(grainNonWholeIdx),
                        wholegrainsHEIFAScoreMale = safeToFloat(wholeGrainScoreMaleIdx),
                        wholegrainsHEIFAScoreFemale = safeToFloat(wholeGrainScoreFemaleIdx),
                        wholegrainsServeSize = safeToFloat(wholeGrainServeSizeIdx),
                        meatAndAlternativesHEIFAScoreMale = safeToFloat(meatScoreMaleIdx),
                        meatAndAlternativesHEIFAScoreFemale = safeToFloat(meatScoreFemaleIdx),
                        meatAndAlternativesWithLegumesAllocatedServeSize = safeToFloat(meatLegServeSizeIdx),
                        legumesAllocatedMeatAndAlternatives = safeToFloat(legAllocMeatIdx),
                        dairyAndAlternativesHEIFAScoreMale = safeToFloat(dairyScoreMaleIdx),
                        dairyAndAlternativesHEIFAScoreFemale = safeToFloat(dairyScoreFemaleIdx),
                        dairyAndAlternativesServeSize = safeToFloat(dairyServeSizeIdx),
                        sodiumHEIFAScoreMale = safeToFloat(sodiumScoreMaleIdx),
                        sodiumHEIFAScoreFemale = safeToFloat(sodiumScoreFemaleIdx),
                        sodiumMgMilligrams = safeToFloat(sodiumMgIdx),
                        alcoholHEIFAScoreMale = safeToFloat(alcoholScoreMaleIdx),
                        alcoholHEIFAScoreFemale = safeToFloat(alcoholScoreFemaleIdx),
                        alcoholStandardDrinks = safeToFloat(alcoholStdDrinksIdx),
                        waterHEIFAScoreMale = safeToFloat(waterScoreMaleIdx),
                        waterHEIFAScoreFemale = safeToFloat(waterScoreFemaleIdx),
                        water = safeToFloat(waterIdx),
                        waterTotalML = safeToFloat(waterTotalMlIdx),
                        beverageTotalML = safeToFloat(bevTotalMlIdx),
                        sugarHEIFAScoreMale = safeToFloat(sugarScoreMaleIdx),
                        sugarHEIFAScoreFemale = safeToFloat(sugarScoreFemaleIdx),
                        sugar = safeToFloat(sugarIdx),
                        saturatedFatHEIFAScoreMale = safeToFloat(satFatScoreMaleIdx),
                        saturatedFatHEIFAScoreFemale = safeToFloat(satFatScoreFemaleIdx),
                        saturatedFat = safeToFloat(satFatIdx),
                        unsaturatedFatHEIFAScoreMale = safeToFloat(unsatFatScoreMaleIdx),
                        unsaturatedFatHEIFAScoreFemale = safeToFloat(unsatFatScoreFemaleIdx),
                        unsaturatedFatServeSize = safeToFloat(unsatFatServeSizeIdx)
                    )
                    patients.add(patient)
                } catch (e: Exception) {
                    println("Error parsing CSV line: ${lines[i]} - Reason: ${e.message}")
                }
            } else {
                println("Skipping malformed CSV line (incorrect number of columns): ${lines[i]}")
            }
        }

        if (patients.isNotEmpty()) {
            withContext(Dispatchers.IO) {
                patientDao.insertAllPatients(patients)
            }
            println("${patients.size} patients seeded into database.")
        } else {
            println("No valid patient data found in CSV to seed.")
        }
    }

    suspend fun isDatabaseSeeded(): Boolean {
        return withContext(Dispatchers.IO) {
            patientDao.getPatientCount() > 0
        }
    }

    suspend fun getPatientByUserId(userId: String): Patient? {
        return withContext(Dispatchers.IO) { patientDao.getPatientById(userId) }
    }

    suspend fun getPatientByIdAndPhone(userId: String, phoneNumber: String): Patient? {
        return withContext(Dispatchers.IO) { patientDao.getPatientByIdAndPhone(userId, phoneNumber) }
    }

    suspend fun claimAccountForPatient(userId: String, phoneNumber: String, name: String, password: String): Boolean {
        return withContext(Dispatchers.IO) {
            patientDao.claimAccount(userId, phoneNumber, name, password) > 0
        }
    }

    fun getAllPatientsFlow(): Flow<List<Patient>> {
        return patientDao.getAllPatientsFlow()
    }

    suspend fun getAverageHeifaScoreMale(): Float? {
        return withContext(Dispatchers.IO) { patientDao.getAverageHeifaScoreMale() }
    }

    suspend fun getAverageHeifaScoreFemale(): Float? {
        return withContext(Dispatchers.IO) { patientDao.getAverageHeifaScoreFemale() }
    }

    suspend fun insertFoodIntake(foodIntake: FoodIntake) {
        withContext(Dispatchers.IO) { foodIntakeDao.insertFoodIntake(foodIntake) }
    }

    fun getFoodIntakeByPatientId(patientId: String): Flow<List<FoodIntake>> {
        return foodIntakeDao.getFoodIntakeByPatientId(patientId)
    }

    suspend fun getLatestFoodIntakeByPatientId(patientId: String): FoodIntake? {
        return foodIntakeDao.getLatestFoodIntakeByPatientId(patientId)
    }

    suspend fun insertNutriCoachTip(tip: NutriCoachTip) {
        withContext(Dispatchers.IO) { nutriCoachTipDao.insertTip(tip) }
    }

    fun getNutriCoachTipsByPatientId(patientId: String): Flow<List<NutriCoachTip>> {
        return nutriCoachTipDao.getTipsByPatientId(patientId)
    }

    fun isNetworkAvailable(): Boolean {
        val connectivityManager = application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
    }

    suspend fun getFruityviceData(fruitName: String): FruitDetails? {
        return if (isNetworkAvailable()) {
            try {
                val response = fruityViceApiService.getFruitDetails(fruitName.lowercase(Locale.getDefault()))
                if (response.isSuccessful) {
                    response.body()
                } else {
                    println("Fruityvice API Error: Code ${response.code()} - ${response.message()} for fruit: $fruitName")
                    null
                }
            } catch (e: Exception) {
                println("Fruityvice API Exception for fruit $fruitName: ${e.message}")
                null
            }
        } else {
            println("Fruityvice API: No network available.")
            null
        }
    }

    suspend fun getGeminiMotivationalTip(prompt: String): String? {
        return if (isNetworkAvailable()) {
            try {
                val response : GenerateContentResponse = generativeModel.generateContent(prompt)
                response.text
            } catch (e: Exception) {
                println("Gemini API Exception (Motivational Tip): ${e.message}")
                "Error generating tip: ${e.localizedMessage}"
            }
        } else {
            println("Gemini API (Motivational Tip): No network available.")
            "Cannot generate tip: No network connection."
        }
    }

    suspend fun getAllPatientDataForAnalysis(): List<Patient> {
        return withContext(Dispatchers.IO) {
            patientDao.getAllPatientsFlow().firstOrNull() ?: emptyList()
        }
    }

    suspend fun getGeminiDataPatternAnalysis(patientDataSummary: String): String? {
        return if (isNetworkAvailable()) {
            try {
                val fullPrompt = """
                    You are an AI data analyst for a nutrition application.
                    Based on the following summary of patient dietary data:
                    $patientDataSummary

                    Please identify exactly three distinct and interesting patterns observed in this data.
                    Specifically focus on:
                    1.  Any potential correlation between high scores in one food group (e.g., Vegetables) and scores in another food group (e.g., Fruit). For instance, do users with high vegetable scores also tend to have high fruit scores?
                    2.  Observable differences or common trends in dietary scores or specific food group consumption patterns between male and female users. For example, do female users show higher average fruit variation scores?
                    3.  A noteworthy general consumption trend for a specific food group or nutrient across the user base (e.g., low wholegrain intake, variable water intake, or high/low discretionary food scores).

                    Present each pattern as a clearly numbered point with a brief explanation.
                    Do not invent data not present in the summary. If the summary lacks detail for a specific point, make a general observation based on what is available or state that more data is needed for that specific insight.

                    Provide your response in the following format:
                    1. [Observed Pattern 1 and explanation]
                    2. [Observed Pattern 2 and explanation]
                    3. [Observed Pattern 3 and explanation]
                """.trimIndent()
                val response = generativeModel.generateContent(fullPrompt)
                response.text
            } catch (e: Exception) {
                println("Gemini Data Pattern Analysis Exception: ${e.message}")
                "Error performing data analysis: ${e.localizedMessage}"
            }
        } else {
            println("Gemini Data Pattern Analysis: No network available.")
            "Cannot perform data analysis: No network connection."
        }
    }
}