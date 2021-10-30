import request from '@/utils/request'

export function fetchList(query) {
  return request({
    url: '/dairyfarm/bucket',
    method: 'get',
    params: query
  })
}

export function createDairyFarm(data) {
  return request({
    url: '/dairyfarm/create',
    method: 'post',
    data
  })
}

