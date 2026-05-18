from fastapi import FastAPI, File, UploadFile, Form
from fastapi.middleware.cors import CORSMiddleware
from fastapi.staticfiles import StaticFiles
from pydantic import BaseModel
from typing import Optional
import sqlite3
import os
import uuid
from datetime import datetime

DB_PATH = os.path.join(os.path.dirname(__file__), "..", "data", "records.db")
os.makedirs(os.path.dirname(DB_PATH), exist_ok=True)

def get_db():
    conn = sqlite3.connect(DB_PATH)
    conn.row_factory = sqlite3.Row
    return conn

def init_db():
    with open(os.path.join(os.path.dirname(__file__), "..", "sql", "init.sql"), "r") as f:
        sql = f.read()
    conn = get_db()
    conn.executescript(sql)
    conn.commit()
    conn.close()

app = FastAPI(title="保安管理系统", version="1.0.0")

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# ========== Gemma API Mock / 接入层 ==========
# TODO: 替换为真实的 Gemma API 调用
# 当前使用 mock：从文件名中提取车牌号模拟识别结果

def mock_gemma_recognize_plate(image_path: str) -> str:
    """
    Mock Gemma 车牌识别。
    真实实现：调用 Gemma API，上传图片，提取车牌号。
    """
    # 模拟：如果文件名包含已知车牌，返回它；否则随机生成一个
    known = ["京A12345", "沪B67890", "粤C11111"]
    for plate in known:
        if plate in image_path:
            return plate
    return "京A88888"

# ========== 数据模型 ==========

class RecordCreate(BaseModel):
    plate_number: str
    phone: str
    building: str
    room: str

class RecordResponse(BaseModel):
    id: int
    plate_number: str
    phone: str
    building: str
    room: str
    created_at: str

class SettingsResponse(BaseModel):
    compound_name: str
    admin_phone: str

class SettingsUpdate(BaseModel):
    compound_name: str
    admin_phone: str

# ========== API 路由 ==========

@app.on_event("startup")
def startup():
    init_db()

@app.post("/api/records", response_model=RecordResponse)
def create_record(record: RecordCreate):
    """录入车辆记录（车牌 + 电话 + 楼栋房间）"""
    conn = get_db()
    cursor = conn.cursor()
    cursor.execute(
        "INSERT INTO records (plate_number, phone, building, room) VALUES (?, ?, ?, ?)",
        (record.plate_number, record.phone, record.building, record.room)
    )
    conn.commit()
    row = cursor.execute("SELECT * FROM records WHERE id = ?", (cursor.lastrowid,)).fetchone()
    conn.close()
    return dict(row)

@app.get("/api/records")
def list_records(q: Optional[str] = None):
    """查询所有记录，支持按车牌号模糊搜索"""
    conn = get_db()
    if q:
        rows = conn.execute(
            "SELECT * FROM records WHERE plate_number LIKE ? ORDER BY created_at DESC",
            (f"%{q}%",)
        ).fetchall()
    else:
        rows = conn.execute("SELECT * FROM records ORDER BY created_at DESC").fetchall()
    conn.close()
    return [dict(r) for r in rows]

@app.post("/api/recognize")
def recognize_plate(file: UploadFile = File(...)):
    """
    上传车牌图片，返回识别的车牌号。
    当前为 Mock 实现，预留 Gemma API 接入。
    """
    temp_path = os.path.join(os.path.dirname(__file__), "..", "data", f"tmp_{uuid.uuid4()}.jpg")
    with open(temp_path, "wb") as f:
        f.write(file.file.read())

    plate = mock_gemma_recognize_plate(temp_path)
    os.remove(temp_path)
    return {"plate_number": plate, "source": "mock"}

@app.post("/api/query-by-image")
def query_by_image(file: UploadFile = File(...)):
    """
    上传图片识别车牌，返回匹配的记录（电话 + 楼栋房间）。
    当前为 Mock 实现，预留 Gemma API 接入。
    """
    temp_path = os.path.join(os.path.dirname(__file__), "..", "data", f"tmp_{uuid.uuid4()}.jpg")
    with open(temp_path, "wb") as f:
        f.write(file.file.read())

    plate = mock_gemma_recognize_plate(temp_path)
    os.remove(temp_path)

    conn = get_db()
    rows = conn.execute(
        "SELECT * FROM records WHERE plate_number = ? ORDER BY created_at DESC",
        (plate,)
    ).fetchall()
    conn.close()

    return {
        "recognized_plate": plate,
        "source": "mock",
        "matches": [dict(r) for r in rows]
    }

@app.delete("/api/records/{record_id}")
def delete_record(record_id: int):
    conn = get_db()
    conn.execute("DELETE FROM records WHERE id = ?", (record_id,))
    conn.commit()
    conn.close()
    return {"success": True}

@app.get("/api/dashboard")
def get_dashboard():
    """Dashboard 统计：今日录入数、总记录数、楼栋统计、最近5条记录"""
    conn = get_db()
    today = datetime.now().strftime("%Y-%m-%d")

    today_count = conn.execute(
        "SELECT COUNT(*) FROM records WHERE date(created_at) = ?", (today,)
    ).fetchone()[0]

    total_count = conn.execute("SELECT COUNT(*) FROM records").fetchone()[0]

    building_rows = conn.execute(
        "SELECT building, COUNT(*) as count FROM records GROUP BY building ORDER BY count DESC"
    ).fetchall()
    building_stats = [{"building": r["building"], "count": r["count"]} for r in building_rows]

    recent_rows = conn.execute(
        "SELECT * FROM records ORDER BY created_at DESC LIMIT 5"
    ).fetchall()
    recent_records = [dict(r) for r in recent_rows]

    conn.close()
    return {
        "today_count": today_count,
        "total_count": total_count,
        "building_stats": building_stats,
        "recent_records": recent_records,
    }

@app.get("/api/settings", response_model=SettingsResponse)
def get_settings():
    """获取系统设置"""
    conn = get_db()
    row = conn.execute("SELECT compound_name, admin_phone FROM settings WHERE id = 1").fetchone()
    conn.close()
    if row is None:
        return {"compound_name": "阳光小区", "admin_phone": ""}
    return {"compound_name": row["compound_name"], "admin_phone": row["admin_phone"]}

@app.put("/api/settings", response_model=SettingsResponse)
def update_settings(settings: SettingsUpdate):
    """更新系统设置"""
    conn = get_db()
    conn.execute(
        "UPDATE settings SET compound_name = ?, admin_phone = ?, updated_at = CURRENT_TIMESTAMP WHERE id = 1",
        (settings.compound_name, settings.admin_phone),
    )
    conn.commit()
    row = conn.execute("SELECT compound_name, admin_phone FROM settings WHERE id = 1").fetchone()
    conn.close()
    return {"compound_name": row["compound_name"], "admin_phone": row["admin_phone"]}

# 挂载静态文件（前端）
app.mount("/", StaticFiles(directory=os.path.join(os.path.dirname(__file__), "..", "web"), html=True), name="static")

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
