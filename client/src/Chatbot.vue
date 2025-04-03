<script setup>
import { computed, ref } from "vue";
import { useAuthStore } from "./infrastructure/stores/auth";
import { GoogleGenerativeAI } from "@google/generative-ai";
import axios from "axios";
import { formatCurrencyVND } from "./utils/common.helper";
import { DingtalkOutlined } from "@ant-design/icons-vue";


// Kiểm tra role người dùng
const authStore = useAuthStore();
const userRole = computed(() => authStore.user.roleName);
const isAdmin = computed(() => userRole.value === "ADMIN");
const isUser = computed(() => userRole.value === "USER");
const isClient = computed(() => userRole.value === "CLIENT");

// 🔹 Khai báo API Key
const API_KEY = import.meta.env.VITE_GEMINI_API_KEY;
const genAI = new GoogleGenerativeAI(API_KEY);
const model = genAI.getGenerativeModel({ model: "gemini-2.0-flash" });

// 🔹 Khai báo trạng thái chatbot
const isOpen = ref(false);
const messages = ref([
  {
    text: `Xin chào! Tôi là TsTalk, tôi có thể giúp gì cho bạn?`,
    sender: "bot",
  },
]);
const userInput = ref("");
const isLoading = ref(false);

// 🔹 Hàm bật/tắt chatbot
const toggleChat = () => {
  isOpen.value = !isOpen.value;
};

const normalizePrice = (priceText) => {
  // Biểu thức chính quy để tìm các từ khóa như "k", "ngàn", "nghìn"
  const normalizedPrice = priceText
    .replace(/(\d+)(k|ngàn|nghìn|ka|cành)/gi, (match, p1, p2) => {
      const multiplier = p2.toLowerCase() === 'k' ? 1000 : 1000;
      return parseInt(p1) * multiplier;
    });
  
  // Chuyển chuỗi thành số
  return parseInt(normalizedPrice.replace(/\D/g, "")); // Loại bỏ các ký tự không phải số
};

// 🔹 Hàm gửi tin nhắn
const sendMessage = async () => {
  if (!userInput.value.trim()) return;

  // Thêm tin nhắn của người dùng vào danh sách
  messages.value.push({ text: userInput.value, sender: "user" });

  isLoading.value = true;
  const inputText = userInput.value;
  userInput.value = "";

  try {
    // Tạo context hội thoại dựa trên vai trò của người dùng
    const commonQuestions = [
      "Bạn có thể giúp tôi tìm sản phẩm?",
      "Tôi muốn mua áo thun",
      "Sản phẩm nào được bán chạy nhất?",
      "Còn sản phẩm nào trong kho không?",
      "Tôi muốn biết thêm về giá sản phẩm này",
      "Có giảm giá gì không?",
      "Tìm áo với màu đỏ và giá dưới 300k",
    ];

    const parts = [
      { text: `Vai trò của người dùng hiện tại là: ${userRole.value}` },
      { text: "Tôi là TsTalk - chatbot của TsT website" },
      {
        text: "Dự án trang web bán hàng Tshirt-Two được thiết kế và phát triển bởi team DATN K19 FPL, do Bùi Minh Hiếu làm Leader",
      },
      //tổng quan
      {
        text: "(Đây là hệ thống quản lý dành cho admin và user) Đây là 1 hệ thống quản lý bán hàng, có thiết kế gọn gàng với các biểu đồ và bảng thống kê.\nCác ô thông tin chính:\n\nTổng đơn thành công (hiển thị số lượng đơn hàng thành công, hiện tại).\nTổng doanh thu (hiển thị tổng số tiền thu được).\nKhách hàng mới (số lượng khách hàng mới).\n\n\nBiểu đồ trạng thái đơn hàng hôm nay: Dạng vòng tròn với 4 trạng thái: Thành công (xanh lá), Chờ xử lý (vàng), Đang giao (xanh dương), Hủy bỏ (đỏ)\nBảng top 5 sản phẩm bán chạy\nMục sản phẩm sắp hết hàng: Có thể hiển thị danh sách các sản phẩm gần hết trong kho.",
      },
      {
        text: "(Đây là hệ thống quản lý dành cho admin và user) Thanh bên trái (Sidebar):\nChứa các mục điều hướng chính, bao gồm:\nTổng quan\nThống kê\nBán tại quầy\nHóa đơn (có menu con: Quản lý hóa đơn, Trả hàng)\nSản phẩm (đang được chọn, có menu con như Danh mục, Thương hiệu, Chất liệu, Cổ áo, Tay áo, Kích cỡ, Màu sắc, Tính năng, Kiểu dáng, Họa tiết)\nĐợt giảm giá, Phiếu giảm giá\nNhân viên, Khách hàng\nKhi một danh mục con được chọn, nó sẽ hiển thị dưới dạng mở rộng trong menu",
      },
      //hóa đơn
      {
        text: '(Đây là hệ thống quản lý dành cho admin và user)Để kiểm tra đơn hàng trong hệ thống này, bạn cần vào mục "Hóa đơn" -> "Quản lý hóa đơn" ở thanh menu bên trái.\nSau khi vào trang Quản lý hóa đơn, bạn có thể:\nLọc đơn hàng theo mã, thông tin khách hàng dựa vào ô tìm kiếm\nLọc đơn hàng loại hóa đơn dựa vào nút radio Loại hóa đơn\nLọc đơn hàng theo ngày dựa vào bộ lọc khoảng ngày\nXem danh sách hóa đơn, bao gồm thông tin như mã hóa đơn, nhân viên phụ trách, khách hàng, tổng tiền, ngày tạo và trạng thái đơn.\nNhấn vào biểu tượng con mắt ở cột cuối cùng để xem chi tiết hóa đơn.',
      },
      {
        text: '(Đây là hệ thống quản lý dành cho admin và user)Màn chi tiết hóa đơn sau khi nhấn vào biểu tượng "Mắt": Hiển thị tiến trình của đơn hàng với các trạng thái:\nChờ xác nhận\nChờ giao hàng\nĐang vận chuyển\nĐã giao hàng\nĐã thanh toán\nThành công\nCác nút thao tác chính:\nChuyển trạng thái đơn hàng\nQuay lại trạng thái trước: Quay về trạng thái trước đó trong quy trình xử lý đơn hàng.\nHủy đơn: Hủy bỏ đơn hàng.Thông tin đơn hàng:Mã đơn hàng,Số điện thoại người nhận, Địa chỉ người nhận, Tên khách hàng, Trạng thái, Tên người nhận, Có nút Chi tiết ở góc phải. Lịch sử thanh toán: Bảng chứa các thông tin: Số tiền khách đưa, Thời gian giao dịch, Mã giao dịch, Phương thức thanh toán, Nhân viên xác nhận: Hiển thị mã nhân viên. Danh sách sản phẩm trong đơn hàng: Bảng hiển thị danh sách sản phẩm đã mua, gồm các cột: Ảnh sản phẩm,Tên sản phẩm, Giá sản phẩm, Số lượng, Thành tiền, Hành động: Nút hoàn hàng nếu trong quá trình chuẩn bị đơn hàng, cửa hàng hoặc shipper muốn hoàn lại, Có nút Thêm sản phẩm',
      },
      //client
      {
        text: `(Đây là trang mua hàng dành cho CLIENT)
            Tiêu đề Trang:
                Các menu như "Trang chủ", "Sản phẩm", "Giới thiệu", "Liên hệ" ở phần trên của trang.
                Thanh Tìm Kiếm: Ở trên cùng, có một thanh tìm kiếm giúp người dùng tìm sản phẩm trên trang.
                Biểu tượng giỏ hàng: Ở trên cùng bên phải, có thể tra cứu thông tin sản phẩm có trong giỏ hàng, thanh toán giỏ hàng.
                Biểu tượng trang cá nhân: Ở góc bên phải trên cùng, có thể tìm kiếm thông tin những đơn hàng đã mua bằng bộ lọc nhập mã đơn hàng hoặc các tab của trạng thái đơn hàng và trạng thái đơn hàng đó.
                Thông tin sản phẩm: Tên sản phẩm: "Áo phông" (đây là tên của sản phẩm được hiển thị lớn).
                Giá sản phẩm: hiển thị phạm vi giá của sản phẩm
                Kích cỡ: hiển thị kích cỡ hiện có của sản phẩm
                Màu sắc:người dùng sẽ chọn màu từ danh sách màu.
                Nút hành động:
                  Thêm vào giỏ: Người dùng có thể thêm sản phẩm vào giỏ hàng.
                  Xem chi tiết: Cho phép người dùng xem thêm chi tiết về sản phẩm.`,
      },
      { text: `input: ${inputText} ` },
    ];

    if (isAdmin.value) {
      parts.push({
        text: "Người dùng là ADMIN, tôi sẽ trả lời những thông tin về hệ thống bán hàng và sản phẩm",
      });
    } else if (isClient.value) {
      parts.push({
        text: `Người dùng là Khách hàng
              - Chỉ cung cấp thông tin về các sản phẩm áo đang được bày bán, nghĩa là tồn tại trong cơ sở dữ liệu của trang Tshirt-Two.`,
      });
    } else {
      parts.push({
        text: "Người dùng là nhân viên (USER), tôi sẽ trả lời những thông tin về hệ thống bán hàng và sản phẩm.",
      });
    }

    // Gọi API chatbot để lấy kết quả trả lời
    const result = await model.generateContent({
      contents: [{ role: "user", parts }],
      generationConfig: {
        temperature: 1,
        topP: 0.95,
        topK: 40,
        maxOutputTokens: 8192,
        responseMimeType: "text/plain",
      },
    });

    const response = await result.response.text();

    const priceMatch = inputText.match(/giá\s+(dưới|trên)?\s*(\d+\s*(k|ngàn|nghìn))/i);
    let maxPrice = 100000000; // Giá trị mặc định nếu không có giá trị nào được tìm thấy

    if (priceMatch) {
      const priceText = priceMatch[2];
      maxPrice = normalizePrice(priceText); // Chuẩn hóa giá trị tiền tệ
    }

    // Gọi API lấy sản phẩm nếu có yêu cầu liên quan đến sản phẩm
    if (
      inputText.toLowerCase().includes("mua") ||
      inputText.toLowerCase().includes("gợi ý") ||
      inputText.toLowerCase().includes("tìm") ||
      inputText.toLowerCase().includes("tìm sản phẩm")
    ) {
      // Tách từ khóa (tên sản phẩm)
      const keywordMatch = inputText.match(/mua\s+([^\s,]+)/i);
      const keyword = keywordMatch ? keywordMatch[1].trim() : null;

      const colorMatch = inputText.match(/màu\s+([a-zA-Zà-ỹ\s,]+)/i);
      const color = colorMatch
        ? colorMatch[1]
            .trim()
            .toLowerCase()
            .replace(/\s*(và|hoặc| )\s*/g, ",") // Thay thế "và" hoặc "hoặc" hoặc dấu cách bằng dấu phẩy
            .split(/\s*,\s*/) // Tách theo dấu phẩy
        : [];

      // Tách giá tối đa
      const priceMatch = inputText.match(/giá\s+(dưới|trên)?\s*(\d+\s*(k|ngàn|nghìn|cành|ka))/i);
    let maxPrice = 100000000; // Giá trị mặc định nếu không có giá trị nào được tìm thấy

    if (priceMatch) {
      const priceText = priceMatch[2];
      maxPrice = normalizePrice(priceText); // Chuẩn hóa giá trị tiền tệ
    }

      // Tìm kiếm kiểu dáng
      const kieuDangMatch = inputText.match(/kiểu dáng\s+([a-zA-Zà-ỹ\s]+)/i);
      const kieuDang = kieuDangMatch
        ? kieuDangMatch[1].trim().toLowerCase()
        : null;

      // Tìm kiếm thương hiệu
      const thuongHieuMatch = inputText.match(/hiệu\s+([a-zA-Zà-ỹ\s]+)/i);
      const thuongHieu = thuongHieuMatch
        ? thuongHieuMatch[1].trim().toLowerCase()
        : null;

      // Tìm kiếm tính năng
      const tinhNangMatch = inputText.match(/tính năng\s+([a-zA-Zà-ỹ\s]+)/i);
      const tinhNang = tinhNangMatch
        ? tinhNangMatch[1].trim().toLowerCase()
        : null;

      // Tìm kiếm kiểu dáng tay áo (cộc tay hoặc dài tay)
      const tayAoMatch = inputText.match(/(cộc tay|dài tay)/i);
      const tayAo = tayAoMatch ? tayAoMatch[0].toLowerCase() : null;

      // Tìm kiếm kiểu cổ áo (ví dụ: "cổ tròn", "cổ V", "cổ áo sơ mi")
      const coAoMatch = inputText.match(/cổ áo\s+([a-zA-Zà-ỹ\s]+)/i);
      const coAo = coAoMatch ? coAoMatch[1].trim().toLowerCase() : null;

      // Tìm kiếm kích cỡ (ví dụ: "S", "M", "L", "XL")
      const kichCoMatch = inputText.match(/kích cỡ\s+([a-zA-Z0-9\s]+)/i);
      const kichCo = kichCoMatch ? kichCoMatch[1].trim().toLowerCase() : null;

      // Gọi API để tìm sản phẩm theo từ khóa (tên sản phẩm hoặc màu sắc)
      const res = await axios.get("http://localhost:3000/other-api/products", {
        params: {
          sanPham: keyword,
          mauSac: color,
          maxPrice: maxPrice,
          tayAo: tayAo,
          coAo: coAo,
          tinhNang: tinhNang,
          thuongHieu: thuongHieu,
          kichCo: kichCo,
          kieuDang: kieuDang,
        }, // Truyền từ khóa vào API tìm kiếm sản phẩm
      });
      // console.log(keyword);
      // console.log(thuongHieu);
      // console.log(color);

      console.log("Kết quả từ API:", res.data);
      if (res.data && res.data.length > 0) {
        const limitedProducts = res.data.slice(0, 5); // giới hạn gợi ý sản phẩm
        const productsList = limitedProducts
          .map(
            (product) =>
              `<li><strong><a href="http://localhost:8888/products/${
                product.id
              }" target="_blank">${product.sanPham}</a></strong>- ${
                product.mauSac
              }</span><br>
                - Giá: <span style="color: green;">${formatCurrencyVND(
                  product.gia
                )}</span><br>
                </li>`
          )
          .join("");

        messages.value.push({
          text: `Tôi tìm thấy các sản phẩm có thể bạn quan tâm: <ul>${productsList}</ul><br>Bạn có thể nhấp vào sản phẩm để đến trang sản phẩm mong muốn`,
          sender: "bot",
        });
      } else {
        messages.value.push({
          text: "Không tìm thấy sản phẩm nào phù hợp.",
          sender: "bot",
        });
      }
      isLoading.value = false;
      return;
    }

    // Thêm câu trả lời từ bot vào danh sách tin nhắn
    const responseBot = "Tôi đã xử lý yêu cầu của bạn. Bạn có muốn tìm thêm thông tin?";
    messages.value.push({
      text: responseBot,
      sender: "bot",
    });
    // Cải thiện sự tương tác
    messages.value.push({
      text: `Bạn có muốn tìm thêm sản phẩm nào không? Hãy cho tôi biết thêm yêu cầu của bạn!`,
      sender: "bot",
    });
  } catch (error) {
    console.error("Lỗi chatbot:", error);
    messages.value.push({
      text: "Xin lỗi, tôi đang gặp sự cố. Hãy thử lại sau!",
      sender: "bot",
    });
  }

  isLoading.value = false;
  localStorage.setItem("chatMessages", JSON.stringify(messages.value));
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
      <span>TsTalk <DingtalkOutlined/></span>
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
