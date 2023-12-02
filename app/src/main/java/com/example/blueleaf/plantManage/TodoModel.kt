package com.example.blueleaf.plantManage


class TodoModel(
    //0 = 물 주기
    //1 = 분갈이 주기
    //2 = 비료 주기
    //3 = 햇빛 주기
    var todo_code: Int = 0,

    //시작 일자(계속해서 업데이트 되어야 함)
    var target_date: String = "",

    //설정한 주기
    var cycle_date: Int = 0
) {
}