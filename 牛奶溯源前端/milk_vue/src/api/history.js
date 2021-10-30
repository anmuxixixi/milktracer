import request from '@/utils/request'

export function fetchList(query) {
  return request({
    url: '/dairyfarm/historylist',
    method: 'get',
    params: query
  })
}

export function fetchQueryList(cowId) {
  return request({
    url: '/dairyfarm/queryhistory',
    method: 'get',
    params: { cowId }
  })
}
