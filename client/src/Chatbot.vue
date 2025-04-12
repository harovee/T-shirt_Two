<script setup>
import { computed, ref } from "vue";
import { useAuthStore } from "./infrastructure/stores/auth";
import { GoogleGenerativeAI } from "@google/generative-ai";
import axios from "axios";
import { formatCurrencyVND } from "./utils/common.helper";
import { DingtalkOutlined } from "@ant-design/icons-vue";

const authStore = useAuthStore();
const userRole = computed(() => authStore.user.roleName);
const isAdmin = computed(() => userRole.value === "ADMIN");
const isUser = computed(() => userRole.value === "USER");
const isClient = computed(() => userRole.value === "CLIENT");

const API_KEY = import.meta.env.VITE_GEMINI_API_KEY;
const genAI = new GoogleGenerativeAI(API_KEY);
const model = genAI.getGenerativeModel({ model: "gemini-2.0-flash" });

// Chat state
const isOpen = ref(false);
const messages = ref([
  {
    text: `Xin chào! Tôi là TsTalk - trợ lý ảo của Tshirt-Two. Tôi có thể giúp gì cho bạn?`,
    sender: "bot",
  },
]);
const userInput = ref("");
const isLoading = ref(false);

// 🔹 Hàm bật/tắt chatbot
const toggleChat = () => {
  isOpen.value = !isOpen.value;
};

// Product knowledge base
const productFeatures = {
  materials: ["cotton", "polyester", "vải cá sấu", "vải jean"],
  sizes: ["S", "M", "L", "XL", "XXL"],
  colors: ["trắng", "đen", "xanh navy", "đỏ", "hồng", "xám", "kem"],
  styles: ["cổ tròn", "cổ tim", "cổ V", "tay ngắn", "tay dài"],
  brands: ["Gucci", "Dior", "Balenciaga", "Local Brand"],
  priceRange: { min: 150000, max: 5000000 },
};

const systemInfo = {
  storeInfo: {
    locations: [
      "Hà Nội: 123 Trần Duy Hưng",
      "TP.HCM: 456 Lê Văn Việt",
      "Đà Nẵng: 789 Ngũ Hành Sơn",
    ],
    openingHours: "9:00 - 21:30 hàng ngày",
    hotline: "1900 1234",
  },
  policies: {
    return: "Đổi trả trong 7 ngày nếu sản phẩm còn nguyên tag",
    shipping: "Miễn phí vận chuyển cho đơn từ 500k",
    payment: "Chấp nhận COD, chuyển khoản và ví điện tử",
  },
  promotions: [
    "Giảm 20% cho đơn đầu tiên - mã WELCOME20",
    "Mua 2 tặng 1 áo phông basic",
    "Freeship toàn quốc thứ 7 hàng tuần",
  ],
};

const normalizeText = (text) => {
  let normalized = text
    .toLowerCase()
    // Xử lý dạng 1tr5 -> 1.5tr
    .replace(/(\d+)tr(\d+)/gi, (_, p1, p2) => {
      const main = parseInt(p1);
      const sub = parseInt(p2) / 10; // Giả sử 1tr5 = 1.5tr
      return `${main + sub}tr`;
    })
    // Xử lý dạng 1tr500 -> 1.5tr
    .replace(/(\d+)tr(\d{3})/gi, (_, p1, p2) => {
      return `${p1}.${p2.slice(0, 1)}tr`;
    });

  normalized = normalized.replace(
    /(\d+[,.]?\d*)\s*?(tr|triệu|k|ngàn|nghìn)\b/gi,
    (match, number, unit) => {
      const num = parseFloat(number.replace(/,/g, "."));
      switch (unit.toLowerCase()) {
        case "tr":
        case "triệu":
          return `${num * 1000000}`;
        case "k":
        case "ngàn":
        case "nghìn":
          return `${num * 1000}`;
        default:
          return match;
      }
    }
  );

  const replacements = {
    "v[ãả]i": "vải",
    "cot\\s*ton": "cotton",
    "gu\\s*ci": "gucci",
    "balen\\s*cia\\s*ga": "balenciaga",
    "\\b(?:sp|sản phẩm)\\b": "sản phẩm",
    "\\b(ao)\\b": "áo",
    "\\b(phong)\\b": "phông",
    "\\b(mau)\\b": "màu",
    "\\b(den)\\b": "đen",
    "\\b(trang)\\b": "trắng",
    "\\b(trag)\\b": "trắng",
    "\\b(so)\\b": "sơ",
    "\\b(mi)\\b": "mi",
    "ao\\s*phong": "áo phông", // "ao phong" → "áo phông"
    "mau\\s*den": "màu đen", // "mau den" → "màu đen" // "quan jean" → "quần jean"
    "ao\\s*so\\s*mi": "áo sơ mi",
  };

  for (const [pattern, replacement] of Object.entries(replacements)) {
    normalized = normalized.replace(new RegExp(pattern, "gi"), replacement);
  }

  normalized = normalized.replace(/,/g, ".");

  console.log("Normalized text:", normalized); // Debug
  return normalized;
};

// Hàm phân tích intent
const analyzeIntent = (text) => {
  const normalized = normalizeText(text);

  const intents = {
    productQuery:
      /(áo|gợi ý|sản phẩm|bán|giá|màu|size|chất liệu|cổ áo|thương hiệu|tay áo|tính năng|kiểu dáng)/i,
    systemInfo:
      /(chính sách|khuyến mãi|cửa hàng|đổi trả|vận chuyển|thanh toán)/i,
    greeting: /(chào|hello|hi|xin chào)/i,
    thanks: /(cảm ơn|thanks|thank you)/i,
  };

  for (const [intent, pattern] of Object.entries(intents)) {
    if (pattern.test(normalized)) return intent;
  }
  return "other";
};

const extractProductInfo = (text) => {
  const normalizedText = normalizeText(text);
  console.log("Normalized text:", normalizedText); // Debug log

  const patterns = {
    sanPham: /(?:sản phẩm|sp|mua|bán|tìm|áo)\s*(\S+)/i,
    mauSac: /(?:màu|sắc)\s*([^,.\d]+)/i,
    kichCo: /(?:size|kích cỡ|kích thước)\s*(\S+)/i,
    maxPrice:
      /(?:giá|khoảng|dưới|trên|tầm)\s*([\d\s.,]+(?:k|ngàn|tr|triệu)?)\b/i,
    chatLieu: new RegExp(`(${productFeatures.materials.join("|")})`, "i"),
    kieuDang: new RegExp(`(${productFeatures.styles.join("|")})`, "i"),
    thuongHieu: new RegExp(`(${productFeatures.brands.join("|")})`, "i"),
    tayAo: new RegExp(`(${productFeatures.brands.join("|")})`, "i"),
    tinhNang: new RegExp(`(${productFeatures.brands.join("|")})`, "i"),
    thuongHieu: new RegExp(`(${productFeatures.brands.join("|")})`, "i"),
  };

  const params = {};

  for (const [key, pattern] of Object.entries(patterns)) {
    const match = normalizedText.match(pattern);
    if (match) {
      // Xử lý riêng cho từng loại
      switch (key) {
        case "color":
          let colors = match[1]
            .trim()
            .split(/\s*(?:và|hoặc|,)\s*/) // Tách bằng "và", "hoặc", dấu phẩy
            .map((color) => {
              // Chuẩn hóa từ đồng nghĩa
              const normalizedColor = color.toLowerCase().replace(/\.$/, "");

              return normalizedColor;
            })
            .filter((color) => productFeatures.colors.includes(color)); // Lọc màu hợp lệ

          if (colors.length > 0) {
            params[key] = colors.length === 1 ? colors[0] : colors;
          }
          break;

        case "size":
          params[key] = match[1].trim();
          break;

        case "price":
          let priceValue = match[1];
          // Xử lý các định dạng số
          priceValue = priceValue
            .replace(/\s+/g, "")
            .replace(/,/g, ".")
            .replace(/[^0-9.]/g, "");

          if (/(k|ngàn|nghìn)$/i.test(match[0])) {
            params.price = Math.round(parseFloat(priceValue) * 1000);
          } else if (/(tr|triệu)$/i.test(match[0])) {
            params.price = Math.round(parseFloat(priceValue) * 1000000);
          } else {
            params.price = Math.round(parseFloat(priceValue));
          }
          break;

        default:
          params[key] = match[1].trim();
      }
    }
  }

  console.log("Extracted params:", params); // Debug log
  return params;
};

const searchProducts = async (searchParams) => {
  try {
    // Xóa các param undefined
    const cleanParams = Object.fromEntries(
      Object.entries(searchParams).filter(([_, v]) => v !== undefined)
    );

    console.log("API call params:", cleanParams); // Debug log

    const res = await axios.get("http://localhost:3000/other-api/products", {
      params: {
        ...cleanParams,
        _limit: 5,
      },
    });

    return res.data;
  } catch (error) {
    console.error("API Error:", error.response?.data || error.message);
    return [];
  }
};

const sendMessage = async () => {
  if (!userInput.value.trim() || isLoading.value) return; // ✅ Chặn click liên tục
  if (!userInput.value.trim()) return;

  const userMessage = userInput.value;
  messages.value.push({ text: userMessage, sender: "user" });
  userInput.value = "";
  isLoading.value = true;

  try {
    const intent = analyzeIntent(userMessage);

    // Xử lý theo intent
    switch (intent) {
      case "greeting":
        messages.value.push({
          text: "Chào bạn! Tôi có thể giúp gì cho bạn về sản phẩm hoặc thông tin cửa hàng?",
          sender: "bot",
        });
        break;

      case "thanks":
        messages.value.push({
          text: "Không có chi! Nếu cần thêm thông tin gì cứ hỏi mình nhé 😊",
          sender: "bot",
        });
        break;

      case "systemInfo": {
        const systemResponse = handleSystemQuery(userMessage);
        messages.value.push({
          text: systemResponse,
          sender: "bot",
        });
        break;
      }

      case "productQuery":
        const searchParams = extractProductInfo(userMessage);

        // Fallback nếu không có param nào
        if (Object.keys(searchParams).length === 0) {
          messages.value.push({
            text: "Bạn có thể nói rõ hơn về sản phẩm cần tìm? (Ví dụ: 'Tìm áo màu đen size L giá 300k')",
            sender: "bot",
          });
          break;
        }

        const products = await searchProducts(searchParams);
        if (products.length > 0) {
          const productList = products
            .map(
              (p) =>
                `<li>
          <a href="http://localhost:8888/products/${p.id}" target="_blank">
            <strong>${p.sanPham}</strong> - ${p.mauSac}
          </a><br>
          Giá: <span style="color: green;">${formatCurrencyVND(p.gia)}</span>
        </li>`
            )
            .join("");

          messages.value.push({
            text: `Tôi tìm thấy ${products.length} sản phẩm có thể phù hợp với bạn:<ul>${productList}</ul>`,
            sender: "bot",
          });
        } else {
          const fallbackResponse = await model.generateContent({
            contents: [
              {
                role: "user",
                parts: [
                  {
                    text: `Hệ thống không có sản phẩm phù hợp. Hãy đề xuất các tùy chọn thay thế dựa trên: 
            - Chất liệu có sẵn: ${productFeatures.materials.join(", ")}
            - Khoảng giá: ${formatCurrencyVND(
              productFeatures.priceRange.min
            )} - ${formatCurrencyVND(productFeatures.priceRange.max)}
            - Thương hiệu: ${productFeatures.brands.join(", ")}`,
                  },
                ],
              },
            ],
          });

          messages.value.push({
            text:
              (await fallbackResponse.response.text()) +
              "\n\nBạn có muốn tìm kiếm với tiêu chí khác không?",
            sender: "bot",
          });
        }
        break;

      default:
        const fallback = await model.generateContent(
          `Hãy trả lời thân thiện: ${userMessage}`
        );
        messages.value.push({
          text: await fallback.response.text(),
          sender: "bot",
        });
    }
  } catch (error) {
    console.error("Chat error:", error);
    messages.value.push({
      text: "Hiện tôi đang gặp chút khó khăn. Bạn vui lòng thử lại sau nhé!",
      sender: "bot",
    });
  } finally {
    isLoading.value = false;
  }
};

const handleSystemQuery = (query) => {
  const normalized = normalizeText(query);
  if (/thanh toán|payment/.test(normalized)) {
    return `💳 Chính sách thanh toán: ${systemInfo.policies.payment}`;
  }

  if (/chính sách.*đổi trả/.test(normalized)) {
    return `📜 Chính sách đổi trả: ${systemInfo.policies.return}`;
  }

  if (/khuyến mãi|ưu đãi/.test(normalized)) {
    return `🎁 Đang có các khuyến mãi:\n${systemInfo.promotions.join("\n- ")}`;
  }

  if (/cửa hàng|địa chỉ/.test(normalized)) {
    return `📍 Hệ thống cửa hàng:\n${systemInfo.storeInfo.locations.join(
      "\n- "
    )}\nGiờ mở cửa: ${systemInfo.storeInfo.openingHours}`;
  }

  if (/vận chuyển|ship/.test(normalized)) {
    return `🚚 Chính sách vận chuyển: ${systemInfo.policies.shipping}`;
  }

  return `ℹ Thông tin hệ thống:\n- Chính sách đổi trả\n- Khuyến mãi\n- Địa chỉ cửa hàng\n- Vận chuyển\nHãy hỏi cụ thể hơn nhé!`;
};
</script>

<template>
  <!-- 🔹 Nút bật chatbot -->
  <a-tooltip title="Chat bot">
    <button class="chat-toggle" @click="toggleChat">
      <DingtalkOutlined />
    </button>
  </a-tooltip>

  <!-- 🔹 Cửa sổ chat -->
  <div v-if="isOpen" class="chat-container">
    <div class="chat-header">
      <span>TsTalk <DingtalkOutlined /></span>
      <button @click="toggleChat">✖</button>
    </div>
    <div class="chat-box">
      <div
        v-for="(msg, index) in messages"
        :key="index"
        :class="msg.sender"
        v-html="msg.text"
      ></div>
      <div v-if="isLoading" class="loading">Đang trả lời...</div>
    </div>
    <div class="chat-input">
      <input
        v-model="userInput"
        @keyup.enter="sendMessage"
        placeholder="Nhập tin nhắn..."
      />
      <button @click="sendMessage" :disabled="isLoading">Gửi</button>
    </div>
  </div>
</template>

<style scoped>
/* 🔹 Nút bật chatbot */
.chat-toggle {
  position: fixed;
  bottom: 80px;
  right: 10px;
  background: #6c757d; /* Xám trung tính */
  color: white;
  border: none;
  width: 50px;
  height: 50px;
  border-radius: 50%;
  font-size: 26px;
  cursor: pointer;
  box-shadow: 0 6px 12px rgba(251, 247, 247, 0.2);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  z-index: 9999;
}

.chat-toggle:hover {
  transform: scale(1.1);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.3);
}

/* 🔹 Cửa sổ chat */
.chat-container {
  position: fixed;
  bottom: 0px;
  right: 50px;
  width: 350px;
  height: 450px;
  /* max-height: 500px; */
  background: #f8f9fa; /* Trắng xám nhạt */
  border-radius: 12px;
  box-shadow: 0 6px 15px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  border: 1px solid #ced4da;
  overflow: hidden;
  animation: fadeIn 0.3s ease-in-out;
  z-index: 9999;
}

/* 🔹 Header chatbot */
.chat-header {
  background: #495057; /* Xám đậm */
  color: white;
  padding: 12px 16px;
  font-weight: bold;
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 16px;
}

.chat-header button {
  background: transparent;
  border: none;
  color: white;
  font-size: 18px;
  cursor: pointer;
  transition: opacity 0.3s;
}

.chat-header button:hover {
  opacity: 0.7;
}

/* 🔹 Nội dung chat */
.chat-box {
  flex-grow: 1;
  padding: 12px;
  overflow-y: auto;
  background: #f1f3f5; /* Xám sáng */
  display: flex;
  flex-direction: column;
  gap: 8px;
  scrollbar-width: thin;
}

.chat-box div {
  padding: 8px 12px;
  margin: 5px;
  border-radius: 10px;
  max-width: 80%;
  word-wrap: break-word;
  font-size: 14px;
}

.user {
  background: #6c757d; /* Xám trung tính */
  color: white;
  align-self: flex-end;
  box-shadow: 0 3px 6px rgba(0, 0, 0, 0.1);
}

.bot {
  background: #dee2e6; /* Xám nhạt */
  color: black;
  align-self: flex-start;
  box-shadow: 0 3px 6px rgba(0, 0, 0, 0.1);
}

.loading {
  text-align: center;
  font-style: italic;
  color: gray;
}

/* 🔹 Ô nhập tin nhắn */
.chat-input {
  display: flex;
  border-top: 1px solid #ced4da;
  padding: 8px;
  background: #f8f9fa;
}

.chat-input input {
  flex-grow: 1;
  padding: 10px;
  border: 1px solid #ced4da;
  border-radius: 20px;
  outline: none;
  transition: all 0.2s ease;
  font-size: 14px;
  background: white;
}

.chat-input input:focus {
  border-color: #6c757d;
  box-shadow: 0 0 6px rgba(108, 117, 125, 0.5);
}

.chat-input button {
  background: #6c757d;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 20px;
  cursor: pointer;
  margin-left: 8px;
  transition: all 0.2s ease;
}

.chat-input button:hover {
  background: #5a6268;
}

/* 🔹 Hiệu ứng xuất hiện */
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
