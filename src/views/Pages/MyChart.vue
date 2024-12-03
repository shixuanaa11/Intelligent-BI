<template>
  <a-input-search
    v-model:value="chartData.name"
    placeholder="请输入图表名称"
    enter-button
    @search="onSearch"
    style="margin-bottom: 16px"
  />
  <a-list
    size="large"
    :pagination="pagination"
    :data-source="resChartData"
    :grid="{ gutter: 2, xs: 1, sm: 1, md: 1, lg: 1, xl: 2, xxl: 2 }"
    :loading="listLoading"
  >
    <template #renderItem="{ item }">
      <a-list-item key="item.title">
        <a-card>
          <a-list-item-meta>
            <template #description>
              <a-tag color="success">{{ '图表类型: ' + item?.chartType }}</a-tag>
              <div style="padding-top: 8px">
                <a-typography-text>{{ '分析目标: ' + item.goal }}</a-typography-text>
              </div>
            </template>

            <template #title>
              <a-typography-title :level="5"> {{ item?.chartName }}</a-typography-title>
            </template>

            <template #avatar><a-avatar :src="item.avatar" /></template>
          </a-list-item-meta>
          <v-chart
            style="width: 600px; height: 300px"
            :option="JSON.parse(item.aiGenChart ?? '{}')"
            autoresize
          />
          <a-collapse v-model:activeKey="activeKey" style="width: 100%">
            <a-collapse-panel :key="item.id" header="AI分析结论">
              <p>{{ item.aiGenResult ?? '' }}</p>
            </a-collapse-panel>
          </a-collapse>
        </a-card>
      </a-list-item>
    </template>
  </a-list>
</template>
<script setup>
import { listMyChartByPage } from '@/api/chart'
import { message } from 'ant-design-vue'

import { onMounted, ref } from 'vue'

const chartData = ref({
  current: 0,
  pageSize: 0,
  sortField: '',
  sortOrder: '',
  id: 0,
  name: '',
  goal: '',
  chartType: '',
  userId: 0,
})

const resChartData = ref([])
const pageTotal = ref(0)

// 列表加载
const listLoading = ref(false)
const loadData = async () => {
  listLoading.value = true
  try {
    const res = await listMyChartByPage(chartData.value)
    console.log('图表列表', res)
    listLoading.value = false

    if (res.code == 0) {
      message.success('获取成功')
      resChartData.value = res.data.records ?? []
      pageTotal.value = res.data.total ?? 0
      console.log('res的数据', resChartData.value)
    } else {
      message.error('获取失败')
    }
  } catch (error) {
    listLoading.value = false
    message.error('获取失败')
    console.log(error)
  }
}

onMounted(() => {
  loadData()
})

// 列表组件属性

const pagination = {
  onChange: (page) => {
    console.log(page)
  },
  pageSize: 4,
}
// 图表
import VChart from 'vue-echarts'
// import { computed, watchEffect } from 'vue'

const activeKey = ref(['1'])

// 搜索
const onSearch = () => {
  loadData()
}
</script>

<style scoped></style>
