<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>系统首页</title>
    <link href="${pageContext.request.contextPath}/static/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body {
            background: #f8f9fa;
            font-family: 'Microsoft YaHei', sans-serif;
            padding: 20px 20px 32px;
            overflow-x: hidden;
        }

        .welcome-card {
            background: #fff;
            border-radius: 12px;
            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
            padding: 24px;
            margin-bottom: 20px;
        }

        .stat-card {
            background: #fff;
            border-radius: 12px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.08);
            padding: 20px;
            text-align: center;
            transition: transform 0.3s ease;
            margin-bottom: 16px;
        }

        .stat-card:hover {
            transform: translateY(-5px);
        }

        .stat-number {
            font-size: 2.4rem;
            font-weight: bold;
            margin-bottom: 10px;
        }

        .stat-label {
            color: #666;
            font-size: 14px;
        }

        .system-info {
            background: #2196f3;
            color: #fff;
            border-radius: 10px;
            padding: 20px;
        }

        .chart-card {
            padding: 24px 24px 18px;
        }

        .chart-title {
            margin: 0 0 16px;
            text-align: center;
            font-size: 24px;
            font-weight: 600;
            line-height: 1.35;
            color: #1f2937;
        }

        .chart-canvas {
            width: 100%;
            height: 360px;
        }

        .chart-canvas.chart-canvas-wide {
            height: 380px;
        }

        @media (max-width: 1199.98px) {
            .chart-title {
                font-size: 22px;
            }
        }

        @media (max-width: 767.98px) {
            body {
                padding: 15px;
            }

            .welcome-card,
            .chart-card {
                padding: 18px;
            }

            .chart-title {
                font-size: 18px;
                margin-bottom: 12px;
            }

            .chart-canvas,
            .chart-canvas.chart-canvas-wide {
                height: 320px;
            }

            .stat-number {
                font-size: 2rem;
            }
        }
    </style>
</head>
<body>
<div class="container-fluid">
    <div class="welcome-card">
        <div class="row align-items-center">
            <div class="col-md-8">
                <h2 class="mb-3">欢迎使用企业员工管理系统</h2>
                <p class="text-muted mb-0"></p>
            </div>
            <div class="col-md-4 text-center">
                <div class="system-info">
                    <h5>系统信息</h5>
                    <p class="mb-1">版本：v1.0.0</p>
                    <p class="mb-0">当前时间：<span id="currentTime"></span></p>
                </div>
            </div>
        </div>
    </div>

    <div class="row mb-2">
        <div class="col-xl-3 col-md-6">
            <div class="stat-card">
                <div class="stat-number text-primary" id="totalUsers">-</div>
                <div class="stat-label">系统用户</div>
            </div>
        </div>
        <div class="col-xl-3 col-md-6">
            <div class="stat-card">
                <div class="stat-number text-success" id="totalEmployees">-</div>
                <div class="stat-label">在职员工</div>
            </div>
        </div>
        <div class="col-xl-3 col-md-6">
            <div class="stat-card">
                <div class="stat-number text-warning" id="totalDepartments">-</div>
                <div class="stat-label">部门数量</div>
            </div>
        </div>
        <div class="col-xl-3 col-md-6">
            <div class="stat-card">
                <div class="stat-number text-info" id="todayAttendance">-</div>
                <div class="stat-label">今日考勤</div>
            </div>
        </div>
    </div>

    <div class="row">
        <div class="col-xl-6 col-lg-12">
            <div class="welcome-card chart-card">
                <h5 class="chart-title">新增人数 & 请假人数（近6个月）</h5>
                <div id="trendBar" class="chart-canvas"></div>
            </div>
        </div>
        <div class="col-xl-6 col-lg-12">
            <div class="welcome-card chart-card">
                <h5 class="chart-title">部门人数比例</h5>
                <div id="deptPie" class="chart-canvas"></div>
            </div>
        </div>
        <div class="col-12">
            <div class="welcome-card chart-card">
                <h5 class="chart-title">今日各部门打卡人数</h5>
                <div id="attPie" class="chart-canvas chart-canvas-wide"></div>
            </div>
        </div>
    </div>
</div>

<script src="${pageContext.request.contextPath}/static/jquery-3.6.1.js"></script>
<script src="https://cdn.jsdelivr.net/npm/echarts@5/dist/echarts.min.js"></script>
<script>
    var charts = {};

    function getChart(id) {
        if (!charts[id] || charts[id].isDisposed()) {
            charts[id] = echarts.init(document.getElementById(id));
        }
        return charts[id];
    }

    function resizeCharts() {
        Object.keys(charts).forEach(function(id) {
            if (charts[id] && !charts[id].isDisposed()) {
                charts[id].resize();
            }
        });
    }

    function renderEmptyChart(chart, text) {
        chart.clear();
        chart.setOption({
            title: {
                text: text,
                left: 'center',
                top: 'middle',
                textStyle: {
                    color: '#9ca3af',
                    fontSize: 16,
                    fontWeight: 'normal'
                }
            }
        }, true);
    }

    function updateTime() {
        var now = new Date();
        var timeString = now.getFullYear() + '-' +
            String(now.getMonth() + 1).padStart(2, '0') + '-' +
            String(now.getDate()).padStart(2, '0') + ' ' +
            String(now.getHours()).padStart(2, '0') + ':' +
            String(now.getMinutes()).padStart(2, '0') + ':' +
            String(now.getSeconds()).padStart(2, '0');
        $('#currentTime').text(timeString);
    }

    function loadStatistics() {
        $.get('${pageContext.request.contextPath}/stats/summary', function(res) {
            if (res.code === 200) {
                var d = res.data;
                $('#totalUsers').text(d.userCount);
                $('#totalEmployees').text(d.employeeCount);
                $('#totalDepartments').text(d.departmentCount);
                $('#todayAttendance').text(d.todayAttendance);
            } else {
                console.error(res.msg);
            }
        });
    }

    function initCharts() {
        loadTrendChart();
        loadDeptChart();
        loadAttChart();
    }

    function loadTrendChart() {
        $.get('${pageContext.request.contextPath}/stats/add-leave-trend', function(res) {
            var chart = getChart('trendBar');
            if (res.code !== 200) {
                renderEmptyChart(chart, '趋势数据加载失败');
                return;
            }

            var months = res.data.months || [];
            if (!months.length) {
                renderEmptyChart(chart, '暂无趋势数据');
                return;
            }

            chart.setOption({
                color: ['#4caf50', '#ff9800'],
                tooltip: {
                    trigger: 'axis',
                    axisPointer: {
                        type: 'shadow'
                    }
                },
                legend: {
                    top: 0,
                    left: 'center',
                    itemWidth: 16,
                    itemHeight: 10,
                    textStyle: {
                        fontSize: 13
                    },
                    data: ['新增', '请假']
                },
                grid: {
                    left: 20,
                    right: 20,
                    bottom: 20,
                    top: 54,
                    containLabel: true
                },
                xAxis: {
                    type: 'category',
                    data: months,
                    axisTick: {
                        alignWithLabel: true
                    },
                    axisLabel: {
                        interval: 0,
                        rotate: 25,
                        margin: 14,
                        fontSize: 12
                    }
                },
                yAxis: {
                    type: 'value',
                    minInterval: 1,
                    axisLabel: {
                        fontSize: 12
                    },
                    splitLine: {
                        lineStyle: {
                            color: '#e5e7eb'
                        }
                    }
                },
                series: [
                    {
                        name: '新增',
                        type: 'bar',
                        data: res.data.add || [],
                        barMaxWidth: 26,
                        itemStyle: {
                            borderRadius: [6, 6, 0, 0]
                        }
                    },
                    {
                        name: '请假',
                        type: 'bar',
                        data: res.data.leave || [],
                        barMaxWidth: 26,
                        itemStyle: {
                            borderRadius: [6, 6, 0, 0]
                        }
                    }
                ]
            }, true);

            setTimeout(function() {
                chart.resize();
            }, 0);
        });
    }

    function loadDeptChart() {
        $.get('${pageContext.request.contextPath}/stats/dept-employee', function(res) {
            var chart = getChart('deptPie');
            if (res.code !== 200) {
                renderEmptyChart(chart, '部门数据加载失败');
                return;
            }

            var rawData = res.data || [];
            var data = rawData.filter(function(item) {
                return item && item.value > 0;
            });

            if (!data.length) {
                renderEmptyChart(chart, '暂无部门人数数据');
                return;
            }

            chart.setOption({
                color: ['#5470c6', '#91cc75', '#fac858', '#ee6666', '#73c0de', '#3ba272', '#fc8452'],
                tooltip: {
                    trigger: 'item',
                    formatter: '{b}: {c}人 ({d}%)'
                },
                legend: {
                    type: 'scroll',
                    orient: 'horizontal',
                    left: 'center',
                    bottom: 0,
                    itemWidth: 16,
                    itemHeight: 10,
                    textStyle: {
                        fontSize: 13
                    },
                    data: data.map(function(item) {
                        return item.name;
                    })
                },
                series: [{
                    type: 'pie',
                    radius: ['42%', '64%'],
                    center: ['50%', '42%'],
                    top: 10,
                    bottom: 55,
                    avoidLabelOverlap: true,
                    minShowLabelAngle: 4,
                    label: {
                        show: true,
                        fontSize: 12,
                        lineHeight: 16,
                        formatter: '{b}\n{d}%'
                    },
                    labelLine: {
                        show: true,
                        length: 14,
                        length2: 10
                    },
                    labelLayout: {
                        hideOverlap: true,
                        moveOverlap: 'shiftY'
                    },
                    data: data,
                    emphasis: {
                        itemStyle: {
                            shadowBlur: 10,
                            shadowOffsetX: 0,
                            shadowColor: 'rgba(0,0,0,0.18)'
                        }
                    }
                }]
            }, true);

            setTimeout(function() {
                chart.resize();
            }, 0);
        });
    }

    function loadAttChart() {
        $.get('${pageContext.request.contextPath}/stats/attendance-dept-checkin', function(res) {
            var chart = getChart('attPie');
            if (res.code !== 200) {
                renderEmptyChart(chart, '考勤数据加载失败');
                return;
            }

            var data = res.data || [];

            if (!data.length) {
                renderEmptyChart(chart, '今日暂无考勤数据');
                return;
            }

            var names = data.map(function(item) {
                return item.name;
            });
            var values = data.map(function(item) {
                return Number(item.value || 0);
            });

            chart.setOption({
                color: ['#10b981'],
                tooltip: {
                    trigger: 'axis',
                    axisPointer: {
                        type: 'shadow'
                    },
                    formatter: function(params) {
                        if (!params || !params.length) {
                            return '';
                        }
                        return params[0].name + '<br/>打卡人数：' + params[0].value + '人';
                    }
                },
                grid: {
                    left: 68,
                    right: 28,
                    bottom: 68,
                    top: 36,
                    containLabel: true
                },
                xAxis: {
                    type: 'category',
                    data: names,
                    axisTick: {
                        alignWithLabel: true
                    },
                    axisLabel: {
                        interval: 0,
                        rotate: 0,
                        margin: 14,
                        fontSize: 12
                    }
                },
                yAxis: {
                    type: 'value',
                    minInterval: 1,
                    name: '人数',
                    nameGap: 18,
                    nameTextStyle: {
                        fontSize: 12,
                        color: '#6b7280',
                        padding: [0, 0, 0, 4]
                    },
                    axisLabel: {
                        fontSize: 12
                    },
                    splitLine: {
                        lineStyle: {
                            color: '#e5e7eb'
                        }
                    }
                },
                series: [{
                    name: '打卡人数',
                    type: 'bar',
                    data: values,
                    barMaxWidth: 46,
                    itemStyle: {
                        borderRadius: [6, 6, 0, 0]
                    },
                    label: {
                        show: true,
                        position: 'top',
                        fontSize: 12,
                        formatter: function(params) {
                            return params.value > 0 ? params.value + '人' : '';
                        }
                    }
                }]
            }, true);

            setTimeout(function() {
                chart.resize();
            }, 0);
        });
    }

    $(document).ready(function() {
        updateTime();
        setInterval(updateTime, 1000);

        loadStatistics();
        setInterval(loadStatistics, 60000);

        initCharts();
        setInterval(initCharts, 300000);

        window.addEventListener('resize', resizeCharts);

        if (window.ResizeObserver) {
            var observer = new ResizeObserver(function() {
                resizeCharts();
            });
            document.querySelectorAll('.chart-canvas').forEach(function(el) {
                observer.observe(el);
            });
        }
    });
</script>
</body>
</html>
