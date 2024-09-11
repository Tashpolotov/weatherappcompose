package com.example.wetherappcompose.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wetherappcompose.R
import com.example.wetherappcompose.ui.theme.BlueLight
import coil.compose.AsyncImage
import com.example.wetherappcompose.data.WeatherModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject


@Composable
fun MainCard(currentDay:MutableState<WeatherModel>, onClickSync: () -> Unit, onClickSearch: () -> Unit) {
    Column(
        modifier = Modifier
            .padding(5.dp)

    ) {
        Card(
            modifier = Modifier
                .padding(top = 36.dp)
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = BlueLight
            ),
            shape = RoundedCornerShape(10.dp)

        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = CenterHorizontally

            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = CenterHorizontally


                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            modifier = Modifier.padding(top = 8.dp, start = 8.dp),
                            text = currentDay.value.time,
                            style = TextStyle(fontSize = 15.sp),
                            color = Color.White
                        )
                        AsyncImage(
                            model = "https:" + currentDay.value.icon,
                            contentDescription = "im2",
                            modifier = Modifier
                                .size(35.dp)
                                .padding(end = 8.dp)

                        )
                    }
                    Text(
                        text = currentDay.value.city,
                        style = TextStyle(fontSize = 24.sp),
                        color = Color.White
                    )

                    Text(
                        text = if(currentDay.value.currentTemp.isNotEmpty())
                            currentDay.value.currentTemp.toFloat().toInt().toString() + "°C"
                            else "${currentDay.value.maxTemp.toFloat().toInt()}°C/${currentDay.value.minTemp}"
                        ,
                        style = TextStyle(fontSize = 65.sp),
                        color = Color.White
                    )
                    Text(
                        text = currentDay.value.condition,
                        style = TextStyle(fontSize = 16.sp),
                        color = Color.White
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween

                        ) {
                        IconButton(
                            onClick = {
                                onClickSearch.invoke()
                            }
                        ) {
                            Icon(painter = painterResource(id = R.drawable.baseline_find_replace_24),
                                contentDescription = "im3",
                                tint = Color.White
                                )
                        }
                        Text(
                            text = "${currentDay.value.maxTemp} ${currentDay.value.minTemp}",
                            style = TextStyle(fontSize = 16.sp),
                            color = Color.White,
                            modifier = Modifier.padding(top = 8.dp)

                            )

                        IconButton(
                            onClick = {
                                onClickSync.invoke()
                            }
                        ){
                            Icon(painter = painterResource(id = R.drawable.ic_refresh),
                                contentDescription = "im4",
                                tint = Color.White
                                )
                        }

                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun TabLayout(daysList: MutableState<List<WeatherModel>>, currentDay: MutableState<WeatherModel>){
    val tabList = listOf("HOURS", "DAYS")
    val pagerState = rememberPagerState()
    val tabIndex = pagerState.currentPage
    val coroutineScope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .padding(start = 5.dp, end = 5.dp)
            .clip(RoundedCornerShape(5.dp))

    ) {
        TabRow(
            selectedTabIndex = tabIndex,
            indicator = {tabPositions->
                TabRowDefaults.Indicator(
                    Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
                )
            },
            backgroundColor = BlueLight,
            contentColor = Color.White
            ) {
            tabList.forEachIndexed { index, text ->
                Tab(
                    selected = false,
                    onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                },
                    text = {
                        Text(text = text)
                    }
                )
            }
        }
        HorizontalPager(
            count = tabList.size,
            state = pagerState,
            modifier = Modifier.weight(1.0f)

        ) { index ->
            val list = when(index){
                0 -> getWeatherByHours(currentDay.value.hours)
                1 -> daysList.value
                else -> daysList.value
            }
            MainList(list, currentDay)

        }
    }
}

private fun getWeatherByHours(hours:String):List<WeatherModel>{
    if(hours.isEmpty()) return listOf()
    val hoursArray = JSONArray(hours)
    val list = ArrayList<WeatherModel>()
    for (i in 0 until hoursArray.length()){
        val item = hoursArray[i] as JSONObject
        list.add(
            WeatherModel(
                "",
                item.getString("time"),
                item.getString("temp_c").toFloat().toInt().toString() + "°C",
                item.getJSONObject("condition").getString("text"),
                item.getJSONObject("condition").getString("icon"),
                "0.0",
                "0.0",
                ""
            )
        )

    }
    return list
}