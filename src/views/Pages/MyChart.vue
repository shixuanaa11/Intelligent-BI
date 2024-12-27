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
    <template #renderItem="{ item, index }">
      <a-list-item key="item.title">
        <a-card style="max-width: 100%; overflow: hidden">
          <a-list-item-meta>
            <template #description>
              <a-tag color="success">{{ '图表类型: ' + item?.chartType }}</a-tag>
              <div style="padding-top: 8px">
                <a-typography-text>{{ '分析目标: ' + item.goal }}</a-typography-text>
              </div>
            </template>

            <template #title>
              <a-flex style="width: 100%" justify="space-between">
                <a-typography-title :level="5"> {{ item?.chartName }}</a-typography-title>
                <a-dropdown>
                  <a @click.prevent>
                    <EllipsisOutlined :style="{ fontSize: '20px' }" />
                  </a>
                  <template #overlay>
                    <a-menu>
                      <a-menu-item>
                        <a href="javascript:;" @click="checkjs(item)">查看JS源码</a>
                      </a-menu-item>
                      <a-menu-item>
                        <a href="javascript:;" @click="Download(index)">下载</a>
                      </a-menu-item>
                      <a-menu-item>
                        <a href="javascript:;" @click="deleteChartbyId(item)">删除</a>
                      </a-menu-item>
                    </a-menu>
                  </template>
                </a-dropdown>
              </a-flex>
            </template>

            <template #avatar><a-avatar :src="item.avatar" /></template>
          </a-list-item-meta>
          <div v-if="item.status == 'succeed'">
            <v-chart
              style="
                width: 100%;
                height: 300px;
                margin-bottom: 16px;
                overflow: hidden; /* 防止内容被裁剪 */
                object-fit: cover;
              "
              :option="chartOption(item)"
              :auto-resize="true"
              :ref="(el) => (chartRefs[index] = el)"
            />
          </div>
          <div v-if="item.status == 'failed'">
            <a-result status="error" title="图表生成失败" :sub-title="item.execMessage"> </a-result>
          </div>

          <div v-if="item.status == 'running'">
            <a-result status="info" title="图表生成中" :sub-title="item.execMessage"> </a-result>
          </div>

          <div v-if="item.status == 'wait'">
            <a-result
              status="warning"
              title="待生成"
              :sub-title="item.execMessage ? item.execMessage : '当前图表生成队列繁忙，请耐心等候'"
            >
            </a-result>
          </div>

          <a-collapse v-model:activeKey="activeKey" style="width: 100%">
            <a-collapse-panel :key="item.id" header="AI分析结论">
              <p>{{ item.aiGenResult ?? '' }}</p>
            </a-collapse-panel>
          </a-collapse>
        </a-card>
      </a-list-item>
      <JscodeModel ref="model"></JscodeModel>
    </template>
  </a-list>
</template>
<script setup>
import { listMyChartByPage, deleteChart } from '@/myapi/chart'
import { message, Modal } from 'ant-design-vue'
import { EllipsisOutlined } from '@ant-design/icons-vue'
import { computed, nextTick, onBeforeUnmount, onMounted, reactive, ref } from 'vue'

const chartData = reactive({
  current: 1,
  pageSize: 6,
  sortField: 'createTime',
  sortOrder: 'descend',
})

const resChartData = ref([])
const pageTotal = ref()

// 列表加载
const listLoading = ref(false)
const loadData = async () => {
  listLoading.value = true
  try {
    const res = await listMyChartByPage({ ...chartData })
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
onBeforeUnmount(() => {
  chartRefs.value = []
})

// 列表组件属性

const pagination = computed(() => {
  return {
    current: chartData.current ?? 1,
    pageSize: chartData.pageSize ?? 10,
    total: pageTotal.value,
    showSizeChanger: true,
    pageSizeOptions: ['6', '12', '18'],
    showTotal: (total) => `共 ${total} 条`,
    onChange: (page, pageSize) => {
      chartData.current = page
      chartData.pageSize = pageSize
      loadData()
    },
  }
})
// 图表
import VChart from 'vue-echarts'
import JscodeModel from '@/components/jscodeModel.vue'
// import { computed, watchEffect } from 'vue'

const activeKey = ref(['1'])

// 搜索
const onSearch = () => {
  loadData()
}
// 删除图表
const deleteChartbyId = (item) => {
  console.log('删除图表', item)
  console.log('删除图表 ID', item.id)
  Modal.confirm({
    title: '删除图表',
    // icon: h(ExclamationCircleOutlined),
    content: '你确定要删除该图表?',
    okText: '确认',
    cancelText: '取消',
    async onOk() {
      try {
        const res = await deleteChart(item.id)
        if (res.code == 0) {
          message.success('删除成功')
          loadData()
        } else {
          message.error('删除失败')
        }
      } catch {
        return console.log('Oops errors!')
      }
    },
    onCancel() {},
  })
}

// 图表
const chartOption = (item) => {
  // console.log('图表函数')
  try {
    // console.log('图表函数')
    const option = JSON.parse(item.aiGenChart ?? '{}')
    // 如果数据点过多，启用滚动条
    option.dataZoom = [
      {
        type: 'inside', // 鼠标滚动缩放
        start: 0,
        end: 20, // 默认显示前 20% 的数据
      },
      {
        type: 'slider',
        show: true,
        start: 0,
        end: 20,
        bottom: 30,
      },
    ]
    option.grid = {
      left: '10%', // 左边距
      right: '10%', // 右边距
      bottom: '40%', // 为滚动条预留空间
    }
    return option
    // return JSON.parse(item.aiGenChart ?? '{}')
  } catch (error) {
    console.error('Failed to parse chart option:', error)
    return null
  }
}
// js代码弹窗ref
const model = ref()
// js代码弹窗
const checkjs = (item) => {
  console.log(model.value)
  console.log(item)

  model.value.openModal()
  model.value.getdata(JSON.parse(item.aiGenChart))
}
// 下载
//动态获取实例
const chartRefs = ref([])

// todo:v-for动态获取实例的问题
const Download = (index) => {
  console.log(index)
  // console.log(chartRefs.value[index])

  nextTick(() => {
    const chartInstance = chartRefs.value[index]

    if (chartInstance) {
      const imageURL = chartInstance.getDataURL({
        pixelRatio: 2,
        backgroundColor: '#fff',
      })
      const link = document.createElement('a')
      link.href = imageURL
      link.download = `chart_${index}.png`
      link.click()
    }
  })
  // const imgData = chartref.value.getDataURL({
  // pixelRatio: 2,
  // backgroundColor: '#fff',
  // })
  // const link = document.createElement('a')
  // link.href = imgData
  // link.download = 'chart.png'
  // link.click()
}
</script>

<style scoped></style>
