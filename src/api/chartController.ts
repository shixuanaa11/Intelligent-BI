// @ts-ignore
/* eslint-disable */
import request from '@/utils/myaxios'

/** 此处后端没有提供注释 POST /chart/delete */
export async function deleteChart(body: API.ChartDeleteRequest, options?: { [key: string]: any }) {
  return request<API.BaseResponseBoolean>('/chart/delete', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /chart/gen */
export async function genChartByAi(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.genChartByAiParams,
  body: {},
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBiResponse>('/chart/gen', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    params: {
      ...params,
      genChartByAiRequest: undefined,
      ...params['genChartByAiRequest'],
    },
    data: body,
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /chart/gen/async */
export async function genChartByAiAsync(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.genChartByAiAsyncParams,
  body: {},
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBiResponse>('/chart/gen/async', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    params: {
      ...params,
      genChartByAiRequest: undefined,
      ...params['genChartByAiRequest'],
    },
    data: body,
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /chart/gen/async/mq */
export async function genChartByAiAsyncMq(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.genChartByAiAsyncMQParams,
  body: {},
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBiResponse>('/chart/gen/async/mq', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    params: {
      ...params,
      genChartByAiRequest: undefined,
      ...params['genChartByAiRequest'],
    },
    data: body,
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /chart/my/list/page */
export async function listMyChartByPage(
  body: API.ChartQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageChart>('/chart/my/list/page', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}
