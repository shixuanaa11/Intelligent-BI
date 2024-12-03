<template>
  <v-chart class="chart" :option="option" autoresize />
</template>

<script setup>
import VChart from 'vue-echarts'
import { computed, watchEffect } from 'vue'
// pinia
import { useChartStore } from '@/stores/chart'

const chartStore = useChartStore()

const option = computed(() => {
  return chartStore.AIGENCHART
})
watchEffect(() => {
  chartStore
})
</script>

<style scoped>
.chart {
  /* height: 100vh; */
  /* width: 400px; */
  height: 300px;
}
</style>
