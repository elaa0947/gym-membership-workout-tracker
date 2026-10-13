const memberId = localStorage.getItem("memberId");
if (!memberId) location.replace("../login.html");
const key = `corely_attendance_${memberId}`;
const read = () => { try { return JSON.parse(localStorage.getItem(key) || "[]"); } catch { return []; } };
const today = new Date();
const dateKey = d => `${d.getFullYear()}-${String(d.getMonth()+1).padStart(2,"0")}-${String(d.getDate()).padStart(2,"0")}`;
const todayKey = dateKey(today);
const pretty = value => new Date(`${value}T12:00:00`).toLocaleDateString(undefined,{year:"numeric",month:"short",day:"numeric"});
const safe = value => String(value).replace(/[&<>"']/g,c=>({"&":"&amp;","<":"&lt;",">":"&gt;",'"':"&quot;","'":"&#39;"}[c]));
function render() {
 const records = read().sort((a,b)=>b.date.localeCompare(a.date));
 const checked = records.some(r=>r.date===todayKey);
 document.getElementById("todayDate").textContent = pretty(todayKey);
 document.getElementById("attendanceStatus").textContent = checked ? "You're checked in for today. Great work." : "You haven't checked in today.";
 const button = document.getElementById("checkInButton");
 button.disabled = checked; button.textContent = checked ? "✓ Checked in today" : "Mark attendance ↗";
 document.getElementById("attendanceTotal").textContent = records.length;
 document.getElementById("attendanceMonth").textContent = records.filter(r=>r.date.startsWith(todayKey.slice(0,7))).length;
 const dates = new Set(records.map(r=>r.date)); let streak = 0;
 for(let d=new Date(today); dates.has(dateKey(d)); d.setDate(d.getDate()-1)) streak++;
 document.getElementById("attendanceStreak").textContent = streak;
 document.getElementById("attendanceHistory").innerHTML = records.length ? records.slice(0,30).map(r=>`<div class="fit-history-row"><span><strong>${safe(pretty(r.date))}</strong><small>Gym check-in recorded</small></span><span class="fit-history-value">PRESENT</span></div>`).join("") : '<p class="fit-empty">No attendance recorded yet. Check in when you visit the gym.</p>';
}
document.getElementById("checkInButton").addEventListener("click",()=>{
 const records=read();
 if(records.some(r=>r.date===todayKey)){render();return;}
 records.push({date:todayKey,createdAt:new Date().toISOString()});
 localStorage.setItem(key,JSON.stringify(records));
 document.getElementById("attendanceMessage").textContent="Attendance recorded on this browser.";
 render();
});
(async()=>{
 try{const r=await fetch(`http://localhost:8080/api/members/profile/${encodeURIComponent(memberId)}`);if(r.ok){const m=await r.json();document.getElementById("userName").textContent=m.name||"Member";document.getElementById("userAvatar").textContent=(m.name||"M").trim().charAt(0).toUpperCase();}}
 catch{}
})();
render();
