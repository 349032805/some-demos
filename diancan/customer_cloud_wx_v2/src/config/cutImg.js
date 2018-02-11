var cutImg = {
    methods : {
        cutImg(url,size,verSize){
            let _url = "";
            if(url){
                let _splitImg = url.split(".");
                let _typeImg = _splitImg[_splitImg.length-1];
                let _resultSplitImg = url.split("."+_typeImg)[0];
                _url = _resultSplitImg+"_"+size+"x"+verSize+"."+_typeImg;
            }
            return _url;
        }
    }
}

export default cutImg