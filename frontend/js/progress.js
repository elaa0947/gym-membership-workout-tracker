const memberId = localStorage.getItem("memberId");
if (!memberId) location.replace("../login.html");
const weightKey=`corely_weights_${memberId}`, workoutKey=`corely_workouts_${memberId}`, attendanceKey=`corely_attendance_${memberId}`;
const read=key=>{try{return JSON.parse(localStorage.getItem(key)||"[]")}catch{return[]}};
const safe=value=>String(value).replace(/[&<>"']/g,c=>({"&":"&amp;","<":"&lt;",">":"&gt;",'"':"&quot;","'":"&#39;"}[c]));
const dateToday=()=>{const d=new Date();return `${d.getFullYear()}-${String(d.getMonth()+1).padStart(2,"0")}-${String(d.getDate()).padStart(2,"0")}`};
document.getElementById("weightDate").value=dateToday();
let profile=null;
function render(){
 const logs=read(weightKey).sort((a,b)=>a.date.localeCompare(b.date));
 document.getElementById("progressWorkouts").textContent=read(workoutKey).length;
 document.getElementById("progressAttendance").textContent=read(attendanceKey).length;
 if(profile){
  const h=Number(profile.height),w=Number(profile.weight);
  document.getElementById("profileHeight").textContent=h>0?`${h} cm`:"—";
  document.getElementById("profileWeight").textContent=w>0?`${w} kg`:"—";
  if(h>0&&w>0){const bmi=w/((h/100)**2);document.getElementById("bmiValue").textContent=bmi.toFixed(1);document.getElementById("bmiNote").textContent="BMI is a screening measure, not a diagnosis."}
  else document.getElementById("bmiNote").textContent="Add height and weight to your member profile to calculate BMI.";
 } else document.getElementById("bmiNote").textContent="Profile unavailable. Check that the backend is running.";
 if(logs.length>=2){const delta=logs[logs.length-1].weight-logs[0].weight;document.getElementById("weightChange").textContent=`${delta>0?"+":""}${delta.toFixed(1)} kg`}
 else document.getElementById("weightChange").textContent="Need 2 logs";
 document.getElementById("weightHistory").innerHTML=logs.length?logs.slice().reverse().map(l=>`<div class="fit-history-row"><span><strong>${safe(l.date)}</strong><small>Weight measurement</small></span><span class="fit-history-value">${Number(l.weight).toFixed(1)} kg</span></div>`).join(""):'<p class="fit-empty">No measurements yet. Save your first weight entry above.</p>';
}
document.getElementById("weightForm").addEventListener("submit",event=>{
 event.preventDefault();
 const weight=Number(document.getElementById("weightInput").value),date=document.getElementById("weightDate").value;
 if(!Number.isFinite(weight)||weight<20||weight>350||!date){document.getElementById("progressMessage").textContent="Enter a valid weight and date.";return}
 const logs=read(weightKey);const existing=logs.findIndex(l=>l.date===date);
 if(existing>=0)logs[existing]={date,weight};else logs.push({date,weight});
 localStorage.setItem(weightKey,JSON.stringify(logs));
 document.getElementById("weightInput").value="";
 document.getElementById("progressMessage").textContent="Measurement saved on this browser.";
 render();
});
(async()=>{
 try{const r=await fetch(`http://localhost:8080/api/members/profile/${encodeURIComponent(memberId)}`);if(!r.ok)throw Error();profile=await r.json();document.getElementById("userName").textContent=profile.name||"Member";document.getElementById("userAvatar").textContent=(profile.name||"M").trim().charAt(0).toUpperCase();}
 catch{document.getElementById("bmiNote").textContent="Profile unavailable. Start the Spring Boot backend to load your measurements."}
 render();
})();
render();
