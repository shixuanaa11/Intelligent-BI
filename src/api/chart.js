// 图表接口
import request from '@/utils/myaxios'
// AI生成图表
export const genChartByAi = (data) =>
  request.post('/chart/gen', data, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  })
// 获取所有图表列表
export const listMyChartByPage = (data) => request.post('/chart/my/list/page', data)
