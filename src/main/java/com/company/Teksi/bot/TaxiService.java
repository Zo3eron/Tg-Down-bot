package com.company.Teksi.bot;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TaxiService extends TelegramLongPollingBot {

    private final UserRepository repository;

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        SendMessage returnMessage = new SendMessage();
        Message message = update.getMessage();
        User user = new User();



        if (update.hasMessage()) {
            Optional<User> optional = this.repository.findById(message.getChatId());

            if (message.getText().equals("/start")) {
                if (optional.isEmpty()) {
                    user.setChatId(update.getMessage().getChatId());
                    user.setUsername(update.getMessage().getChat().getUserName());
                    user.setFirstName(message.getChat().getFirstName());
                    repository.save(user);
                }
                returnMessage = stageLangauge(message);
            }else if (message.getText().equals("пассажир\uD83E\uDDCD\u200D♂\uFE0F")) {
                returnMessage = StageClentRu(message);
                if (optional.isPresent()) {
                    User newuser = optional.get();
                    newuser.setLanguage(BotQuery.LANGUAGE_RU);
                    newuser.setType(message.getText());
                    newuser.setStep(BotQuery.WRITE_TEXT);
                    repository.save(newuser);
                }
            } else if (message.getText().equals("Yolovchi\uD83E\uDDCD\u200D♂\uFE0F")) {
                returnMessage = StageClentUz(message);
                if (optional.isPresent()) {
                    User newuser = optional.get();
                    newuser.setLanguage(BotQuery.LANGUAGE_UZ);
                    newuser.setType(message.getText());
                    newuser.setStep(BotQuery.WRITE_TEXT);
                    repository.save(newuser);
                }
            } else if (message.getText().equals("водитель\uD83D\uDE95")) {
                if (optional.isPresent()) {
                    User newuser = optional.get();
                    newuser.setLanguage(BotQuery.LANGUAGE_RU);
                    newuser.setType(message.getText());
                    newuser.setStep(BotQuery.TAXIS_LOGIN);
                    repository.save(newuser);
                }
                returnMessage = stageAboutVipRu(message);
            } else if (message.getText().equals("Haydovchi\uD83D\uDE95")) {
                if (optional.isPresent()) {
                    User newuser = optional.get();
                    newuser.setLanguage(BotQuery.LANGUAGE_UZ);
                    newuser.setType(message.getText());
                    newuser.setStep(BotQuery.TAXIS_LOGIN);
                    repository.save(newuser);
                }
                returnMessage = stageAboutVipUz(message);
            }
            else if (optional.get().getStep().equals(BotQuery.WRITE_TEXT)) {
               if(update.getMessage().getChat().getUserName() == null){
                    if (message.getText().matches("\\+998\\d{9}")) {
                        if (optional.isPresent()) {
                            User newuser = optional.get();
                            newuser.setUsername(message.getText());
                            newuser.setStep(BotQuery.LOK_TEXT);
                            repository.save(newuser);
                        }
                        if (optional.get().getLanguage().equals(BotQuery.LANGUAGE_UZ)) {
                            returnMessage = stageLockatsionUz(message);
                        } else if (optional.get().getLanguage().equals(BotQuery.LANGUAGE_RU)) {
                            returnMessage = stageLockatsionRu(message);
                        }
                    }else if (optional.get().getLanguage().equals(BotQuery.LANGUAGE_UZ)) {
                       returnMessage.setChatId(message.getChatId());
                       returnMessage.setText("Sizning username yoq bolganiuchun nomer yozib qoldiring \n Misol uchun: +99891507XXXX  +998 bilan boshlanishi shart.❌");

                   } else if (optional.get().getLanguage().equals(BotQuery.LANGUAGE_RU)) {
                       returnMessage.setChatId(message.getChatId());
                       returnMessage.setText("Если вам нравится ваше имя пользователя, запишите номер\n" +
                               "  Например: +99891507XXXX должен начинаться с +998.❌");
                   }/*
                   if (optional.get().getLanguage().equals(BotQuery.LANGUAGE_UZ)) {
                       returnMessage.setChatId(message.getChatId());
                       returnMessage.setText("Nomer tolliq yozib qoldiring \n Misol uchun: +99891507XXXX  +998 bilan boshlanishi shart.❌");

                   } else if (optional.get().getLanguage().equals(BotQuery.LANGUAGE_RU)) {
                       returnMessage.setChatId(message.getChatId());
                       returnMessage.setText("Запишите полный номер. \n Например: +99891507XXXX должен начинаться с +998.❌\"");
                   }*/
               }else {
                   if (optional.isPresent()) {
                       User newuser = optional.get();
                       newuser.setMsg(message.getText());
                       newuser.setStep(BotQuery.LOK_TEXT);
                       repository.save(newuser);
                   }
                   if (optional.get().getLanguage().equals(BotQuery.LANGUAGE_UZ)) {
                       returnMessage = stageLockatsionUz(message);
                   } else if (optional.get().getLanguage().equals(BotQuery.LANGUAGE_RU)) {
                       returnMessage = stageLockatsionRu(message);
                   }
               }
            } else if (message.getText().equals("Назад") || message.getText().equals("Orqaga")) {
                if (optional.get().getLanguage().equals(BotQuery.LANGUAGE_UZ)) {
                    returnMessage = stageStarUz(String.valueOf(message.getChatId()));
                } else if (optional.get().getLanguage().equals(BotQuery.LANGUAGE_RU)) {
                    returnMessage = stageStarRu(String.valueOf(message.getChatId()));
                }
                if (optional.isPresent()) {
                    User newuser = optional.get();
                    newuser.setStep(BotQuery.START_ROLE);
                    repository.save(newuser);
                }
            } else if (message.getText().equals("Данные VIP группы‼\uFE0F") || message.getText().equals("Vip haqida ‼\uFE0F")) {
                if (optional.get().getLanguage().equals(BotQuery.LANGUAGE_UZ)) {
                    returnMessage = stepAboutUz(message);
                } else if (optional.get().getLanguage().equals(BotQuery.LANGUAGE_RU)) {
                    returnMessage = stepAboutRu(message);
                }
            } else if (optional.get().getStep().equals(BotQuery.LOK_TEXT) && message.getText().equals("Из Бекабада в Ташкент") || message.getText().equals("Toshkentdan Bekobodga") || message.getText().equals("Bekoboddan Toshkentga") || message.getText().equals("Из Ташкента в Бекабад")) {
                if (optional.isPresent()) {
                    User secuser = optional.get();
                    secuser.setLocation(message.getText());
                    secuser.setStep(BotQuery.ORDER_TEXT);
                    repository.save(secuser);
                }
                if (optional.get().getLanguage().equals(BotQuery.LANGUAGE_UZ)) {
                    returnMessage = stageOrderUz(message);
                } else if (optional.get().getLanguage().equals(BotQuery.LANGUAGE_RU)) {
                    returnMessage = stageOrderRu(message);
                }
            } else if (optional.get().getStep().equals(BotQuery.ORDER_TEXT) || message.getText().equals("почта\uD83D\uDCE6") || message.getText().equals("1 персона\uD83D\uDC64") || message.getText().equals("2 персона\uD83D\uDC65") || message.getText().equals("3 персона\uD83D\uDC68\u200D\uD83D\uDC68\u200D\uD83D\uDC66") || message.getText().equals("4 персона\uD83D\uDC68\u200D\uD83D\uDC68\u200D\uD83D\uDC66\u200D\uD83D\uDC66") || message.getText().equals("1 kishi\uD83D\uDC64") || message.getText().equals("2 kishi\uD83D\uDC65") || message.getText().equals("3 kishi\uD83D\uDC68\u200D\uD83D\uDC68\u200D\uD83D\uDC66") || message.getText().equals("4 kishi\uD83D\uDC68\u200D\uD83D\uDC68\u200D\uD83D\uDC66\u200D\uD83D\uDC66")) {
                if (optional.isPresent()) {
                    User secuser = optional.get();
                    secuser.setCount(message.getText());
                    secuser.setStep(BotQuery.YES_OR_NO);
                    repository.save(secuser);
                }
                if (optional.get().getLanguage().equals(BotQuery.LANGUAGE_UZ)) {
                    returnMessage.setText(optional.get().getType() + "\n\n1\uFE0F⃣ Malumot :  " + optional.get().getMsg() + "\n2\uFE0F⃣ telegram manzili: @" + optional.get().getUsername() + "\n3\uFE0F⃣  Yonalishi:  " + optional.get().getLocation() + "\n4\uFE0F⃣ yolovchi yoki pochta: " + optional.get().getCount());
                    returnMessage.setChatId(String.valueOf(message.getChatId()));
                    execute(returnMessage);
                    returnMessage = stageEndUZ(message);
                } else if (optional.get().getLanguage().equals(BotQuery.LANGUAGE_RU)) {
                    returnMessage.setText(optional.get().getType() + "\n\n1\uFE0F⃣ Malumot :  " + optional.get().getMsg() + "\n2\uFE0F⃣ telegram manzili:  @" + optional.get().getUsername() + "\n3\uFE0F⃣  Yonalishi:  " + optional.get().getLocation() + "\n4\uFE0F⃣ yolovchi yoki pochta:  " + optional.get().getCount());
                    returnMessage.setChatId(String.valueOf(message.getChatId()));
                    execute(returnMessage);
                    returnMessage = stageEndRu(message);
                }
            } else if (optional.get().getStep().equals(BotQuery.YES_OR_NO) && message.getText().equals("Нет") || message.getText().equals("Yoq")) {
                if (optional.get().getLanguage().equals(BotQuery.LANGUAGE_UZ)) {
                    returnMessage = stageStarUz(String.valueOf(message.getChatId()));
                } else if (optional.get().getLanguage().equals(BotQuery.LANGUAGE_RU)) {
                    returnMessage = stageStarRu(String.valueOf(message.getChatId()));
                }
            } else if (optional.get().getStep().equals(BotQuery.YES_OR_NO) && message.getText().equals("Xa") || message.getText().equals("Да")) {
                SendMessage sendMessage = new SendMessage();
                if (optional.get().getMsg().isEmpty()){
                    sendMessage.setText(optional.get().getType() + "\n2\uFE0F⃣ telegram manzili:  @" + optional.get().getUsername() + "\n3\uFE0F⃣  Yonalishi:  " + optional.get().getLocation() + "\n4\uFE0F⃣ Soni yoki pochta:  " + optional.get().getCount());
                }else {
                    sendMessage.setText(optional.get().getType() + "\n\n1\uFE0F⃣  Malumot:  " + optional.get().getMsg() + "\n2\uFE0F⃣ telegram manzili:  @" + optional.get().getUsername() + "\n3\uFE0F⃣  Yonalishi:  " + optional.get().getLocation() + "\n4\uFE0F⃣ soni yoki pochta:  " + optional.get().getCount());
                }
                sendMessage.setChatId("-1002075492453");
                execute(sendMessage);

                sendMessage.setText("Xurmatli  " + optional.get().getFirstName() +
                        "\n Sizning zakasingiz shafyorlar guruhiga tushdi\n" +
                        "\n" +
                        "Lichkangizda Ishonchlik shafyorlarimiz kutmoqda\n" +
                        "\n" +
                        "Qulaylik uchun bot orqali zakas bering\uD83D\uDC47");
                sendMessage.setChatId("-1001534568289");
                InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
                List<InlineKeyboardButton> rowInline = new ArrayList<>();
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText("Bot orqali taksi chaqirish");
                button.setUrl("https://t.me/Bekobod_Toshkent_taxi_bot");
                rowInline.add(button);
                rowsInline.add(rowInline);
                markupInline.setKeyboard(rowsInline);

                sendMessage.setReplyMarkup(markupInline);
                execute(sendMessage);
                if (optional.get().getLanguage().equals(BotQuery.LANGUAGE_UZ)) {
                    returnMessage = stageEndUz(String.valueOf(message.getChatId()));
                } else if (optional.get().getLanguage().equals(BotQuery.LANGUAGE_RU)) {
                    returnMessage = stageEndRu(String.valueOf(message.getChatId()));
                }
            }
            else {
                returnMessage.setText("Mavjud bolmagan query jonatildi");
                returnMessage.setChatId(String.valueOf(message.getChatId()));
            }
        }
    else if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();

            String data = callbackQuery.getData();


            returnMessage.setChatId(update.getCallbackQuery().getFrom().getId());

            String chadId = update.getCallbackQuery().getFrom().getId().toString();
            DeleteMessage deleteMessage = new DeleteMessage();
            if (data.equals(BotQuery.LANGUAGE_UZ)) {

                returnMessage = stageStarUz(chadId);
                deleteMessage.setChatId(String.valueOf(callbackQuery.getMessage().getChatId()));
                deleteMessage.setMessageId(callbackQuery.getMessage().getMessageId());

            } else if (data.equals(BotQuery.LANGUAGE_RU)) {
                returnMessage = stageStarRu(chadId);
                deleteMessage.setChatId(String.valueOf(callbackQuery.getMessage().getChatId()));
                deleteMessage.setMessageId(callbackQuery.getMessage().getMessageId());

            }
            execute(deleteMessage);
        }


        execute(returnMessage);

    }

    private SendMessage stageEndUZ(Message message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Barcha malumotlar to'g'rimi");
        sendMessage.setChatId(String.valueOf(message.getChatId()));

        KeyboardRow firstRow = new KeyboardRow();
        KeyboardRow thirdRow = new KeyboardRow();

        KeyboardButton first = new KeyboardButton();
        first.setText("Xa");
        KeyboardButton second = new KeyboardButton();
        second.setText("Yoq");

        firstRow.add(first);
        thirdRow.add(second);

        List<KeyboardRow> rowList = new ArrayList<>();
        rowList.add(firstRow);
        rowList.add(thirdRow);

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setKeyboard(rowList);

        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        return sendMessage;
    }

    private SendMessage stageEndRu(Message message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Barcha malumotlar to'g'rimi");
        sendMessage.setChatId(String.valueOf(message.getChatId()));

        KeyboardRow firstRow = new KeyboardRow();
        KeyboardRow thirdRow = new KeyboardRow();

        KeyboardButton first = new KeyboardButton();
        first.setText("Да");
        KeyboardButton second = new KeyboardButton();
        second.setText("Нет");

        firstRow.add(first);
        thirdRow.add(second);

        List<KeyboardRow> rowList = new ArrayList<>();
        rowList.add(firstRow);
        rowList.add(thirdRow);

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setKeyboard(rowList);

        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        return sendMessage;
    }

    private SendMessage stageOrderRu(Message message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(BotText.ORDER_RU);
        sendMessage.setChatId(String.valueOf(message.getChatId()));

        KeyboardRow firstRow = new KeyboardRow();
        KeyboardRow thirdRow = new KeyboardRow();
        KeyboardRow secRow = new KeyboardRow();
        KeyboardRow duRow = new KeyboardRow();

        KeyboardButton first = new KeyboardButton();
        first.setText("почта\uD83D\uDCE6");
        KeyboardButton second = new KeyboardButton();
        second.setText("1 персона\uD83D\uDC64");
        KeyboardButton sec = new KeyboardButton();
        sec.setText("2 персона\uD83D\uDC65");
        KeyboardButton thr = new KeyboardButton();
        thr.setText("3 персона\uD83D\uDC68\u200D\uD83D\uDC68\u200D\uD83D\uDC66");
        KeyboardButton frt = new KeyboardButton();
        frt.setText("4 персона\uD83D\uDC68\u200D\uD83D\uDC68\u200D\uD83D\uDC66\u200D\uD83D\uDC66");
        KeyboardButton back = new KeyboardButton();
        back.setText("Orqaga");

        firstRow.add(first);
        thirdRow.add(second);
        thirdRow.add(sec);
        secRow.add(thr);
        secRow.add(frt);
        duRow.add(back);

        List<KeyboardRow> rowList = new ArrayList<>();
        rowList.add(firstRow);
        rowList.add(thirdRow);
        rowList.add(secRow);
        rowList.add(duRow);

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setKeyboard(rowList);

        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        return sendMessage;
    }

    private SendMessage stageOrderUz(Message message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(BotText.ORDER_UZ);
        sendMessage.setChatId(String.valueOf(message.getChatId()));

        KeyboardRow firstRow = new KeyboardRow();
        KeyboardRow thirdRow = new KeyboardRow();
        KeyboardRow secRow = new KeyboardRow();
        KeyboardRow duRow = new KeyboardRow();

        KeyboardButton first = new KeyboardButton();
        first.setText("Pochta\uD83D\uDCE6");
        KeyboardButton second = new KeyboardButton();
        second.setText("1 kishi\uD83D\uDC64");
        KeyboardButton sec = new KeyboardButton();
        sec.setText("2 kishi\uD83D\uDC65");
        KeyboardButton thr = new KeyboardButton();
        thr.setText("3 kishi\uD83D\uDC68\u200D\uD83D\uDC68\u200D\uD83D\uDC66");
        KeyboardButton frt = new KeyboardButton();
        frt.setText("4 kishi\uD83D\uDC68\u200D\uD83D\uDC68\u200D\uD83D\uDC66\u200D\uD83D\uDC66");
        KeyboardButton back = new KeyboardButton();
        back.setText("Orqaga");

        firstRow.add(first);
        thirdRow.add(second);
        thirdRow.add(sec);
        secRow.add(thr);
        secRow.add(frt);
        duRow.add(back);

        List<KeyboardRow> rowList = new ArrayList<>();
        rowList.add(firstRow);
        rowList.add(thirdRow);
        rowList.add(secRow);
        rowList.add(duRow);

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setKeyboard(rowList);

        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        return sendMessage;
    }

    private SendMessage stageLockatsionUz(Message message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(BotText.CLENT_LOK_UZ);
        sendMessage.setChatId(String.valueOf(message.getChatId()));

        KeyboardRow firstRow = new KeyboardRow();
        KeyboardRow thirdRow = new KeyboardRow();

        KeyboardButton first = new KeyboardButton();
        first.setText("Toshkentdan Bekobodga");
        KeyboardButton second = new KeyboardButton();
        second.setText("Bekoboddan Toshkentga");

        firstRow.add(first);
        thirdRow.add(second);

        List<KeyboardRow> rowList = new ArrayList<>();
        rowList.add(firstRow);
        rowList.add(thirdRow);

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setKeyboard(rowList);

        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        return sendMessage;
    }

    private SendMessage stageLockatsionRu(Message message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(BotText.CLENT_LOK_RU);
        sendMessage.setChatId(String.valueOf(message.getChatId()));

        KeyboardRow firstRow = new KeyboardRow();
        KeyboardRow thirdRow = new KeyboardRow();

        KeyboardButton first = new KeyboardButton();
        first.setText("Из Ташкента в Бекабад");
        KeyboardButton second = new KeyboardButton();
        second.setText("Из Бекабада в Ташкент");

        firstRow.add(first);
        thirdRow.add(second);

        List<KeyboardRow> rowList = new ArrayList<>();
        rowList.add(firstRow);
        rowList.add(thirdRow);

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setKeyboard(rowList);

        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        return sendMessage;
    }

    private SendMessage stepAboutRu(Message message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(BotText.VIPABOUT_RU);
        sendMessage.setChatId(String.valueOf(message.getChatId()));

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> buttons = new ArrayList<>();


        InlineKeyboardButton button1 = new InlineKeyboardButton();
        button1.setText("Подключиться к Vip");
        button1.setUrl("https://t.me/Sanjar_Kamilovich");
        buttons.add(button1);

        rows.add(buttons);
        inlineKeyboardMarkup.setKeyboard(rows);

        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        return sendMessage;
    }

    private SendMessage stepAboutUz(Message message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(BotText.VIPABOUT_UZ);
        sendMessage.setChatId(String.valueOf(message.getChatId()));

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> buttons = new ArrayList<>();


        InlineKeyboardButton button1 = new InlineKeyboardButton();
        button1.setText("Vipga ulanish");
        button1.setUrl("https://t.me/Sanjar_Kamilovich");
        buttons.add(button1);

        rows.add(buttons);
        inlineKeyboardMarkup.setKeyboard(rows);

        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        return sendMessage;
    }


    private SendMessage stageAboutVipRu(Message message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(BotText.TAXITEXT_RU);
        sendMessage.setChatId(String.valueOf(message.getChatId()));

        KeyboardRow firstRow = new KeyboardRow();
        KeyboardRow thirdRow = new KeyboardRow();

        KeyboardButton vipInfoButton = new KeyboardButton();
        vipInfoButton.setText("Данные VIP группы‼\uFE0F");
        KeyboardButton backButton = new KeyboardButton();
        backButton.setText("Назад");

        firstRow.add(vipInfoButton);
        thirdRow.add(backButton);

        List<KeyboardRow> rowList = new ArrayList<>();
        rowList.add(firstRow);
        rowList.add(thirdRow);

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setKeyboard(rowList);

        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        return sendMessage;
    }

    private SendMessage stageAboutVipUz(Message message) {

        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(BotText.TAXITEXT_UZ);
        sendMessage.setChatId(String.valueOf(message.getChatId()));

        KeyboardRow firstRow = new KeyboardRow();
        KeyboardRow thirdRow = new KeyboardRow();

        KeyboardButton vipInfoButton = new KeyboardButton();
        vipInfoButton.setText("Vip haqida ‼\uFE0F");
        KeyboardButton backButton = new KeyboardButton();
        backButton.setText("Orqaga");

        firstRow.add(vipInfoButton);
        thirdRow.add(backButton);

        List<KeyboardRow> rowList = new ArrayList<>();
        rowList.add(firstRow);
        rowList.add(thirdRow);

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setKeyboard(rowList);

        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        return sendMessage;
    }

    private SendMessage StageClentRu(Message message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(BotText.TextNumRu);
        sendMessage.setChatId(String.valueOf(message.getChatId()));
        return sendMessage;
    }

    private SendMessage StageClentUz(Message message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(BotText.TextNumUz);
        sendMessage.setChatId(String.valueOf(message.getChatId()));

        return sendMessage;
    }

    private SendMessage stageStarRu(String chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Главное меню \n" +
                " \n" +
                "Вы водитель или пассажир?");
        sendMessage.setChatId(chatId);
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        List<KeyboardRow> rowList = new ArrayList<>();
        KeyboardRow first = new KeyboardRow();
        KeyboardButton firstButton = new KeyboardButton();
        firstButton.setText("водитель\uD83D\uDE95");
        KeyboardButton secButton = new KeyboardButton();
        secButton.setText("пассажир\uD83E\uDDCD\u200D♂\uFE0F");
        first.add(firstButton);
        first.add(secButton);
        rowList.add(first);
        replyKeyboardMarkup.setKeyboard(rowList);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);

        return sendMessage;
    }

    private SendMessage stageStarUz(String chadId) {

        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Bosh menu \n" +
                " \n" +
                "Haydovchimisiz yoki Yulovchi?");
        sendMessage.setChatId(chadId);
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        List<KeyboardRow> rowList = new ArrayList<>();
        KeyboardRow first = new KeyboardRow();
        KeyboardButton firstButton = new KeyboardButton();
        firstButton.setText("Haydovchi\uD83D\uDE95");
        KeyboardButton secButton = new KeyboardButton();
        secButton.setText("Yolovchi\uD83E\uDDCD\u200D♂\uFE0F");
        first.add(firstButton);
        first.add(secButton);
        rowList.add(first);
        replyKeyboardMarkup.setKeyboard(rowList);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);

        return sendMessage;
    }
    private SendMessage stageEndRu(String chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Хорошие такси вам понравятся");
        sendMessage.setChatId(chatId);
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        List<KeyboardRow> rowList = new ArrayList<>();
        KeyboardRow first = new KeyboardRow();
        KeyboardButton firstButton = new KeyboardButton();
        firstButton.setText("водитель\uD83D\uDE95");
        KeyboardButton secButton = new KeyboardButton();
        secButton.setText("пассажир\uD83E\uDDCD\u200D♂\uFE0F");
        first.add(firstButton);
        first.add(secButton);
        rowList.add(first);
        replyKeyboardMarkup.setKeyboard(rowList);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);

        return sendMessage;
    }

    private SendMessage stageEndUz(String chadId) {

        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Yaxshi sizga taksislar murojat qiladi");
        sendMessage.setChatId(chadId);
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        List<KeyboardRow> rowList = new ArrayList<>();
        KeyboardRow first = new KeyboardRow();
        KeyboardButton firstButton = new KeyboardButton();
        firstButton.setText("Haydovchi\uD83D\uDE95");
        KeyboardButton secButton = new KeyboardButton();
        secButton.setText("Yolovchi\uD83E\uDDCD\u200D♂\uFE0F");
        first.add(firstButton);
        first.add(secButton);
        rowList.add(first);
        replyKeyboardMarkup.setKeyboard(rowList);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);

        return sendMessage;
    }

    private SendMessage stageLangauge(Message message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Iltimos tilni tanlang \n Пожалуйста, выберите язык");
        sendMessage.setChatId(String.valueOf(message.getChatId()));

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> buttons = new ArrayList<>();

        // Uz tili
        InlineKeyboardButton button1 = new InlineKeyboardButton();
        button1.setText("Uz\uD83C\uDDFA\uD83C\uDDFF");
        button1.setCallbackData(BotQuery.LANGUAGE_UZ);
        buttons.add(button1);

        // Ru Tili
        InlineKeyboardButton button2 = new InlineKeyboardButton();
        button2.setText("Ru \uD83C\uDDF7\uD83C\uDDFA");
        button2.setCallbackData(BotQuery.LANGUAGE_RU);
        buttons.add(button2);

        rows.add(buttons);
        inlineKeyboardMarkup.setKeyboard(rows);

        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        return sendMessage;
    }

    @Override
    public String getBotUsername() {
        return "@Bekobod_Toshkent_taxi_bot";
    }

    @Override
    public String getBotToken() {
        return "6342415204:AAGicl3TD8cX8kXXus3DcTiLc-zUpM2S0nM";
    }
}

