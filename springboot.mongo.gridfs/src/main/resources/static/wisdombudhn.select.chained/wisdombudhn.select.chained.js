var WisdombudhnSelectChained = {
    init() {
        var me = this;
        var wscs = document.getElementsByClassName('wisdombudhn.select.chained');
        for (let index = 0; index < wscs.length; index++) {
            const element = wscs[index];
            me.appendChildSelects(element, index);
        }
    },
    /**
     * 
     * @param {*} control 
     * @param {*} controlIndex 
     */
    appendChildSelects(control, controlIndex) {
        var $control = $(control)
        if (control.dataset.controlStyle) {
            $control.addClass(control.dataset.controlStyle);
        }
        let strCount = control.dataset.selectCount;
        let numberCount = 0;
        if (strCount && typeof strCount === "string") {
            numberCount = Number(strCount);
        }
        if (numberCount == 0) {
            return;
        }

        var lables = [];
        if (control.dataset.controlLable) {
            lables = control.dataset.controlLable.split('-');
        }

        for (let index = 0; index < numberCount; index++) {
            var $select = $('<select class="wisdombudhn.select" name="select' + controlIndex + '-' + index +
                '" id="select' + controlIndex + '-' + index + '" style="width:100px;"></select>')
            if (control.dataset.selectStyle) {
                $select.addClass(control.dataset.selectStyle);
            }

            $control.append($select);
            if (index < lables.length) {
                control.append(lables[index]);
            }

        }
    },
    /**
     * 
     * @param {*} selectList jquery 集合 
     * @param {*} data 
     * @param {*} jquery 控件对象 
     */
    bindControl(selectList, data, $control) {
        var me = this;
        // 这里selectList为依次级联的选择器元素列表，如[select1,select2,select3,...]  
        for (var i = 0; i < selectList.length; i++) {
            var temp_data = data;

            for (var j = 0; j < i; j++) {
                if (temp_data[0] && temp_data[0].list) {
                    temp_data = temp_data[0].list;
                }
            }
            me.fillSelect(selectList[i], temp_data);
            //增加变更事件  
            selectList[i].addEventListener("change", function(event) {
                var value = event.target.value;
                console.debug("event.target.value:" + event.target.value);
                let splitStr = "-";
                if ($control && $control[0].dataset.controlValueSplit) {
                    splitStr = $control[0].dataset.controlValueSplit;
                }

                var v_p = value.split(splitStr);
                var v_length = v_p.length;
                //如果是最后一个select就跳出  
                if (v_length >= selectList.length) {
                    return;
                }
                //构造新的选择器  
                var newSelectList = [];
                for (var j = v_length; j < selectList.length; j++)
                    newSelectList.push(selectList[j]);

                //构造新的数据  
                var newData = data;
                for (var j = 0; j < v_p.length; j++) {
                    var curr = newData[parseInt(v_p[j]) - 1];
                    if (curr && curr.list) {
                        newData = newData[parseInt(v_p[j]) - 1].list;
                    }
                }

                console.debug("newSelectList:");
                console.debug(newSelectList);
                console.debug("newData:");
                console.debug(newData);

                me.bindControl(newSelectList, newData, $control);
            });
        }

    },
    /**
     * 
     * @param {*} select 
     * @param {*} list 
     */
    fillSelect(select, list) {
        if (!select || !list) {
            return;
        }
        select.innerHTML = '';
        list.forEach(function(item) {
            var option = new Option(item.text, item.value);
            select.add(option);
        });
    }
};

WisdombudhnSelectChained.init();