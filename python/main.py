from tkinter import *
import tkinter as tk

root = Tk()
root.title("Jacobi V2")
root.geometry("500x700")
root.configure(background="#550369")


def Jacobi_Starts():
    try:
        ###############################################################################
        number_of_equations = 0.0
        list_of_chars = []
        equation_list = []
        multiple_answer_list = []

        def Get_Equations(count_of_Equations):
            for n in range(1, count_of_Equations+1):
                starti = float(n)
                endi = float(n+1)
                equation = input_number_of_equation.get(
                    str(starti), str(endi)).lower()
                equation = equation.replace(" ", "")
                if not (equation[0] == "-" or equation[0] == "+"):
                    equation = "+" + equation
                if n == 1:
                    for m in range(len(equation)-1):
                        if "a" <= equation[m] <= "z":
                            list_of_chars.append(equation[m])
                equation = equation.replace("\n", "")
                finalEquation = ""
                for v in range(len(equation)):
                    if ("a" <= equation[v] <= "z"):
                        if not("0" <= equation[v-1] <= "9"):
                            finalEquation = equation[:v]
                            finalEquation = finalEquation + "1"
                            finalEquation = finalEquation + equation[v:]
                            equation = finalEquation
                equation_list.append(equation)

        def Simplify_Equations(count_of_Equations):
            for n in range(count_of_Equations):
                Single_Equation_Simplification(n)

        def Single_Equation_Simplification(equation_number):
            flag_for_save_place = False
            flag_equal = False
            equation = equation_list[equation_number]
            new_equation = ""
            temp = ""
            equal_index = 0
            equation_number += 1
            for n in range(len(equation)):
                if equation[n] == "=":
                    flag_equal = True
                if flag_equal:
                    new_equation += equation[n]

            i = 0
            count_of_PM = 0
            while True:
                if equation[i] == "=":
                    break
                if equation[i] == "+" or equation[i] == "-":
                    count_of_PM += 1
                if count_of_PM == equation_number:
                    flag_for_save_place = True
                else:
                    flag_for_save_place = False
                if not flag_for_save_place:
                    if equation[i] == "+":
                        new_equation += "-"
                    elif equation[i] == "-":
                        new_equation += "+"
                    else:
                        new_equation += equation[i]
                else:
                    temp += equation[i]
                i += 1
            new_equation = temp + new_equation
            for y in range(len(new_equation)-1):
                if "0" <= new_equation[y] <= "9" and "a" <= new_equation[y+1] <= "z":
                    new_equation = list(new_equation)
                    new_equation.insert(y+1, "*")
                    new_equation = ''.join(new_equation)
            equal_index = new_equation.index("=")
            tempSize = len(new_equation[(equal_index+1):])
            tempST = new_equation[(equal_index+1):]
            newTempSt = ""
            for t in range(tempSize-1):
                if not(t == tempSize - 1):
                    if ("0" <= tempST[t] <= "9" and "a" <= tempST[t+1] <= "z"):
                        newTempSt = tempST[:t+1]
                        newTempSt = newTempSt + "*"
                        newTempSt = newTempSt + tempST[t+1:]
            new_equation = new_equation[equal_index-1] + "=(" + newTempSt + ")/(" + \
                           new_equation[:(equal_index-1)].replace("*", "") + ")"
            equation_number -= 1
            print(new_equation)
            equation_list[equation_number] = new_equation

        def Get_Answers_Input():
            answers = input_answers_of_equation.get()
            answers = answers.replace(" ", "")
            for i in range(len(answers)):
                multiple_answer[i] = int(answers[i])
            print(multiple_answer)
            multiple_answer_list.append(multiple_answer)

        def Main_Calculation(count_of_Equations):
            new_answers = []
            for n in range(count_of_Equations):
                new_answers.append(Calculate_Single_Equation(n))
            multiple_answer_list.append(new_answers)

        def Calculate_Single_Equation(equation_number):
            prev_answer = multiple_answer_list[len(multiple_answer_list)-1]
            equation = equation_list[equation_number]
            equation = equation[2:]
            for n in range(len(list_of_chars)):
                if equation_number != (n):
                    locals()[list_of_chars[n]] = prev_answer[n]
            return(round(eval(equation), 7))

        number_of_equations = float(input_number_of_equation.index('end'))-1.0
        while True:
            if not ("a" <= input_number_of_equation.get(number_of_equations) <= "z" or "0" <= input_number_of_equation.get(number_of_equations) <= "9" or "+" == input_number_of_equation.get(number_of_equations) or "-" == input_number_of_equation.get(number_of_equations)):
                number_of_equations -= 1
            else:
                break
        multiple_answer = [None] * int(number_of_equations)
        Get_Equations(int(number_of_equations))
        Simplify_Equations(int(number_of_equations))
        Get_Answers_Input()
        Main_Calculation(int(number_of_equations))
        while multiple_answer_list[(len(multiple_answer_list)-1)] != multiple_answer_list[(len(multiple_answer_list)-2)]:
            Main_Calculation(int(number_of_equations))
        t.insert(END, str(multiple_answer_list))
        temp = multiple_answer_list[len(multiple_answer_list)-1]
        final_ans_str = ""
        for s in range(len(temp)):
            final_ans_str = final_ans_str+str(temp[s]) + " / "
        final_ans_str = final_ans_str[:-3]
        print(final_ans)
        tp.insert(END, final_ans_str)
        ###############################################################################
    except ValueError:
        answer.config(text="fail")


first_input = Label(root, text="Enter Your Equations:", font=(
    "Helvetica", 16), fg="black", bg="white")
first_input.pack(pady=5)

input_number_of_equation = Text(root, width=40, height=3, bg="white", fg="black", font=(
    "Helvetica", 16))
input_number_of_equation.pack(pady=5)

sec_in_frame = Frame(root)
sec_in_frame.pack(pady=5)
second_input = Label(sec_in_frame, text="Enter First Multiple Answers:", font=(
    "Helvetica", 16), fg="black", bg="white")
second_input.grid(row=0, column=0)

input_answers_of_equation = Entry(
    sec_in_frame, bg="white", fg="black", font=("Helvetica", 16), highlightthickness=1)
input_answers_of_equation.config(
    highlightbackground="black", highlightcolor="black")
input_answers_of_equation.grid(row=0, column=1, padx=1)

confirm_button = Button(root, text="Click To Calculate",
                        height=1, command=Jacobi_Starts, font=("Helvetica", 16))
confirm_button.pack(pady=5)


answer_arr_input = Label(root, text="Sequence Of Answers:", font=(
    "Helvetica", 16), fg="black", bg="white")
answer_arr_input.pack(pady=40)

answer = Label(root, height=2, text="        ")
answer.pack(pady=5)

h = Scrollbar(answer, orient='horizontal')
h.pack(side=BOTTOM, fill=X)
t = Text(answer, height=3, width=30, wrap=NONE,
         xscrollcommand=h.set,
         font=("Helvetica", 18))
t.pack(side=TOP, fill=X)
h.config(command=t.xview)
last_input = Label(root, text="Your Final Answer Is:", font=(
    "Helvetica", 18), fg="black", bg="white")
last_input.pack(pady=20)
final_ans = Label(root, height=1, width=20, text="        ")
final_ans.pack(pady=5)
hp = Scrollbar(final_ans, orient='horizontal')
hp.pack(side=BOTTOM, fill=X)
tp = Text(final_ans, height=3, width=30, wrap=NONE,
          xscrollcommand=hp.set,
          font=("Helvetica", 18), fg="red")
tp.pack(side=TOP, fill=X)
hp.config(command=tp.xview)


root.mainloop()
