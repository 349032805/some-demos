import Qs from 'qs'
import axios from "axios"
var url = location.origin

// let token = "BYsQqt1498803472932";
//let token = window.localStorage.getItem("token");
//axios.defaults.headers.common['token'] = token;
axios.defaults.baseURL = url;

axios.defaults.timeout = 15000;
// axios.defaults.transf ormRequest = [function(data) {
//   data = Qs.stringify(data)
//   return data
// }]

axios.defaults.transformResponse = [function (data) {
    data = JSON.parse(data)
    return data
}]

axios.interceptors.request.use(function (config) {
    console.log("请求开始")
    return config;
}, function (error) {
    return Promise.reject(error);
});

axios.interceptors.response.use((res) => {
    console.log("请求结束 ")
    return res;
}, (error) => {
    return Promise.reject(error);
});

function mapAxios(type, url, params) {
    return new Promise((resolve, reject) => {
        axios({
            method: 'get',
            url: url,
            params: params
        }).then(res => {
            resolve(res.data)
        }).catch(err => {
            if (err == "Error: timeout of 15000ms exceeded") {
                console.log("服务器请求超时")
                return
            }
            alert(err)
        })
    })
}


export default mapAxios
