//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.SessionAttributes;
//
//// Assuming you use session management to determine if the user is logged in
//@SessionAttributes("user") // Tracks 'user' object in the session
//@RequestMapping("/account")
//public class AccountController {
//
//    @GetMapping("/profile")
//    public String profilePage(Model model) {
//        // Check if the user is logged in by checking the session attribute
//        boolean isLoggedIn = model.containsAttribute("user");
//
//        // Add the 'isLoggedIn' attribute to the model
//        model.addAttribute("isLoggedIn", isLoggedIn);
//
//        return "profile"; // Return the Thymeleaf view
//    }
//}
