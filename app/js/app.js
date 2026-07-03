// ===== Course Data =====
var courseData = {
  '地标': [
    { title: '平津战役纪念馆VR体验',     meta: '地标模块 · 小学段', desc: '通过VR技术，让同学们身临其境地感受平津战役的壮阔历史场景，铭记革命先辈的英勇事迹。' },
    { title: '天津古文化街红色足迹',     meta: '地标模块 · 小学段', desc: '走进古文化街，探寻隐藏在市井街巷中的红色足迹，感受津门文化的红色基因。' },
    { title: '解放桥的故事',             meta: '地标模块 · 中学段', desc: '了解解放桥的历史变迁，感受天津解放的伟大时刻，传承革命精神。' },
  ],
  '人物': [
    { title: '周恩来邓颖超纪念馆',       meta: '人物模块 · 中学段', desc: '学习周恩来总理和邓颖超同志的革命事迹和崇高精神，厚植爱国情怀。' },
    { title: '劳模精神进课堂',           meta: '人物模块 · 小学段', desc: '聆听天津劳模故事，传承爱岗敬业、争创一流、艰苦奋斗的劳模精神。' },
    { title: '天津抗日英雄谱',           meta: '人物模块 · 大学段', desc: '介绍天津地区的抗日英雄人物及其感人事迹，缅怀先烈、珍爱和平。' },
  ],
  '文物': [
    { title: '津派文物中的红色记忆',     meta: '津派文物 · 中学段', desc: '通过天津地区珍贵革命文物，讲述背后的红色故事，让历史"活"起来。' },
  ],
  '事件': [
    { title: '天津抗日救亡运动',         meta: '历史事件 · 大学段', desc: '回顾天津人民在抗日战争中的英勇斗争历程，弘扬民族精神和爱国情怀。' },
    { title: '平津战役',                 meta: '历史事件 · 中学段', desc: '了解平津战役的历史背景、经过及其重大意义，铭记新中国成立的艰难历程。' },
  ],
};

// ===== Page Router =====
function openPage(pageId, param) {
  // Hide all pages
  var pages = document.querySelectorAll('.page');
  var i;
  for (i = 0; i < pages.length; i++) {
    pages[i].classList.remove('show');
  }

  // Show target page
  var target = document.getElementById(pageId);
  if (target) target.classList.add('show');

  // Update tab bar
  var tabNames = ['pageHome', 'courseList', 'pageFeature', 'pageProfile'];
  var tabs = document.querySelectorAll('.tab-item');
  for (i = 0; i < tabs.length; i++) {
    tabs[i].classList.toggle('active', tabNames[i] === pageId);
  }

  // Course list: load data
  if (pageId === 'courseList' && param) {
    document.getElementById('listTitle').textContent = param + ' · 课程列表';
    renderList(param);
  }

  // Course detail: fill data
  if (pageId === 'courseDetail' && param) {
    fillDetail(param);
  }

  // Scroll top
  window.scrollTo(0, 0);
}

// ===== Render Course List =====
function renderList(module) {
  var list = courseData[module] || [];
  var container = document.getElementById('courseListEl');
  var html = '';
  var i;
  for (i = 0; i < list.length; i++) {
    var c = list[i];
    html += '<div class="course-card" onclick="openPage(\'courseDetail\',\'' + c.title + '\')">'
          + '<div class="course-thumb">&#9654;</div>'
          + '<div class="course-info">'
          + '<div class="course-title">' + c.title + '</div>'
          + '<div class="course-meta">' + c.meta + '</div>'
          + '</div></div>';
  }
  container.innerHTML = html;
}

// ===== Fill Course Detail =====
function fillDetail(title) {
  // Find course by title
  var all = [];
  var keys = Object.keys(courseData);
  var i, j;
  for (i = 0; i < keys.length; i++) {
    var list = courseData[keys[i]];
    for (j = 0; j < list.length; j++) {
      all.push(list[j]);
    }
  }
  var course = null;
  for (i = 0; i < all.length; i++) {
    if (all[i].title === title) { course = all[i]; break; }
  }
  if (!course) course = { title: title, meta: '课程模块', desc: '课程详细介绍待补充。' };

  document.getElementById('detailTitle').textContent = course.title;
  document.getElementById('detailMeta').textContent  = course.meta;
  document.getElementById('detailDesc').textContent  = course.desc;
}

// ===== Filter Tabs =====
function filterTab(el) {
  var tabs = document.querySelectorAll('.filter-tab');
  var i;
  for (i = 0; i < tabs.length; i++) {
    tabs[i].classList.remove('active');
  }
  el.classList.add('active');
}

// ===== Coming Soon =====
function showComingSoon(name) {
  alert(name + ' 功能即将上线，敬请期待！');
}

// ===== Splash Screen =====
window.addEventListener('DOMContentLoaded', function() {
  updateTime();
  setInterval(updateTime, 30000);

  setTimeout(function() {
    var splash = document.getElementById('splash');
    splash.classList.add('hide');
    setTimeout(function() { splash.style.display = 'none'; }, 600);
  }, 2200);
});

function updateTime() {
  var now = new Date();
  var h   = String(now.getHours()).padStart(2, '0');
  var m   = String(now.getMinutes()).padStart(2, '0');
  var els = document.querySelectorAll('#sTime,#sTime2');
  var i;
  for (i = 0; i < els.length; i++) {
    els[i].textContent = h + ':' + m;
  }
}
