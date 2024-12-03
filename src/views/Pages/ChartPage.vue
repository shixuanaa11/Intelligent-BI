<template>
  <a-row :gutter="24" justify="space-between">
    <a-col :span="11">
      <a-card title="AI智能分析">
        <a-form
          :labelCol="{ span: 4 }"
          :model="formState"
          name="validate_other"
          v-bind="formItemLayout"
          @finishFailed="onFinishFailed"
          @finish="onFinish"
        >
          <a-form-item label="图表名称">
            <a-input v-model:value="formState.ChartName" />
          </a-form-item>
          <a-form-item name="goal" label="分析目标">
            <a-textarea v-model:value="formState.goal" />
          </a-form-item>

          <a-form-item
            name="ChartType"
            label="图表类型"
            has-feedback
            :rules="[{ required: true, message: 'Please select your country!' }]"
          >
            <a-select
              v-model:value="formState.ChartType"
              placeholder="Please select a country"
              :options="options"
            >
            </a-select>
          </a-form-item>

          <a-form-item name="file" label="原始数据">
            <a-upload
              v-model:file-list="formState.file"
              name="file"
              :max-count="1"
              before-upload="false"
            >
              <a-button>
                <upload-outlined></upload-outlined>
                上传CSV文件
              </a-button>
            </a-upload>
          </a-form-item>
          <a-form-item :wrapper-col="{ span: 12, offset: 4 }">
            <a-button type="primary" html-type="submit" :loading="loading">提交</a-button>
            <a-button style="margin-left: 10px" @click="resetFields">重置</a-button>
          </a-form-item>
        </a-form>
      </a-card>
    </a-col>

    <a-col :span="12">
      <a-card title="分析结论">{{ result ? result : '请点击左侧提交' }} </a-card>
      <Divider />
      <a-card title="可视化图表">
        <Genchart></Genchart>
      </a-card>
    </a-col>
  </a-row>
</template>
<script setup>
import { reactive, ref, toRef, watchEffect } from 'vue'
import { UploadOutlined } from '@ant-design/icons-vue'
import { Divider, message } from 'ant-design-vue'
import Genchart from '@/components/chart.vue'
// 接口
import { genChartByAi } from '@/api/chart'
// pinia
import { useChartStore } from '@/stores/chart'
const chartStore = useChartStore()
// 按钮加载
const loading = ref(false)
const formState = ref({
  file: [],
  goal: '',
  ChartName: '',
  ChartType: '',
})
// ai生成结果
const result = ref('')

// ai生成的图表
// const chartData = chartStore.AIGENCHART
const onFinish = async () => {
  loading.value = true
  console.log('formState:', formState.value)
  // 传入的第一个文件
  const file = formState.value.file[0]?.originFileObj
  const formData = new FormData()
  formData.append('file', file)
  formData.append('goal', formState.value.goal)
  formData.append('chartName', formState.value.ChartName)
  formData.append('chartType', formState.value.ChartType)
  console.log(formData)

  const res = await genChartByAi(formData).catch((err) => {
    console.log(err)
    loading.value = false
  })
  console.log(res)
  if (res?.code == 0) {
    message.success('提交成功')
    // result.value = res.data.aiGenResult
    chartStore.SET_AIGENRESULT(res.data.aiGenResult)
    result.value = res.data.aiGenResult
    chartStore.SET_AIGENCHART(JSON.parse(res.data.aiGenChart))
    console.log('结论数据', res.data.aiGenResult)
    console.log('pinia的结论数据', chartStore.AIGENRESULT)
  } else {
    message.error('提交失败')
  }
  loading.value = false
}

const onFinishFailed = (errorInfo) => {
  console.log('Failed:', errorInfo)
}
// 下拉框数据
const options = ref([
  {
    value: '折线图',
    label: '折线图',
  },
  {
    value: '柱状图',
    label: '柱状图',
  },
  {
    value: '堆叠图',
    label: '堆叠图',
  },
  {
    value: '饼图',
    label: '饼图',
  },
  {
    value: '雷达图',
    label: '雷达图',
  },
])
// 重置
const resetFields = () => {
  loading.value = false
  formState.value = {
    file: [],
    goal: '',
    ChartName: '',
    ChartType: '',
  }
}
// const handleChange = (info) => {
//   if (info.file.status === 'done') {
//     message.success(`${info.file.name} 上传成功`)
//   } else if (info.file.status === 'error') {
//     message.error(`${info.file.name} 上传失败.`)
//   }
// }
</script>
<style scoped></style>
