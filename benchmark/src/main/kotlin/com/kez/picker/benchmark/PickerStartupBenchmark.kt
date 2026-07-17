package com.kez.picker.benchmark

import android.content.Intent
import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

private const val TargetPackage = "com.kez.picker.benchmark.target"
private const val TargetActivity = "$TargetPackage.PickerBenchmarkActivity"
private const val BenchmarkScenarioExtra = "$TargetPackage.BENCHMARK_SCENARIO"
private const val BenchmarkIterations = 10

@LargeTest
@RunWith(AndroidJUnit4::class)
class PickerStartupBenchmark {

    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun generic10Items() = benchmarkScenario("generic-10")

    @Test
    fun generic100Items() = benchmarkScenario("generic-100")

    @Test
    fun generic10000Items() = benchmarkScenario("generic-10000")

    @Test
    fun dateDefault9000Years() = benchmarkScenario("date-default")

    @Test
    fun dateCustomExact9000Years() = benchmarkScenario("date-custom-exact")

    private fun benchmarkScenario(scenario: String) {
        benchmarkRule.measureRepeated(
            packageName = TargetPackage,
            metrics = listOf(StartupTimingMetric()),
            iterations = BenchmarkIterations,
            compilationMode = CompilationMode.Full(),
            startupMode = StartupMode.COLD
        ) {
            startActivityAndWait(
                Intent(Intent.ACTION_MAIN).apply {
                    setClassName(TargetPackage, TargetActivity)
                    putExtra(BenchmarkScenarioExtra, scenario)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                }
            )
        }
    }
}
