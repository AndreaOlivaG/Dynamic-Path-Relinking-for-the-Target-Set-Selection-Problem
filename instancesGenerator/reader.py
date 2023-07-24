from math import ceil, floor

def read_ranking_instance(file_name, influence, precision):
    file = open(file_name, "r")
    n = int(file.readline())
    rankings = [None] * n
    psi = [None] * n
    for i in range(n):
        rankings[i] = list(map(float, file.readline().split()))
        psi[i] = [0.0] * n
    file.close()
    rank = n * (n - 1) / 2
    for i in range(n):
        for j in range(n):
            if i != j:
                psi[j][i] = min(1.0,  round(influence * (n - rankings[i][j]) / rank, precision))
    return psi

def read_event_attendance_instance(file_name, influence, precision):
    file = open(file_name, "r")
    n, m = map(int, file.readline().split())
    attendance = [None] * n
    for i in range(n):
        attendance[i] = list(map(int, file.readline().split()))
    file.close()
    events = [None] * n
    psi = [None] * n
    for i in range(n):
        events[i] = [0.0] * n
        psi[i] = [0.0] * n
        for j in range(n):
            if i != j:
                for k in range(m):
                    if attendance[i][k] == 1 and attendance[j][k] == 1:
                        events[i][j] += 1.0
    for i in range(n):
        attend = 0.0
        for j in range(n):
            attend += events[j][i]
        if attend > 0:
            for j in range(n):
                psi[j][i] = min(1.0, round(influence * events[j][i] / attend, precision)) 
    return psi

def read_interaction_instance(file_name, influence, precision):
    file = open(file_name, "r")
    n = int(file.readline())
    interactions = [None] * n
    psi = [None] * n
    for i in range(n):
        interactions[i] = list(map(float, file.readline().split()))
        psi[i] = [0.0] * n
    file.close()
    for i in range(n):
        total = 0
        for j in range(n):
            total += interactions[j][i]
        if total > 0:
            for j in range(n):
                psi[j][i] = min(1.0, round(influence * interactions[j][i] / total, precision))
    return psi

def read_transpose_interaction_instance(file_name, influence, precision):
    file = open(file_name, "r")
    n = int(file.readline())
    interactions = [None] * n
    psi = [None] * n
    for i in range(n):
        interactions[i] = list(map(float, file.readline().split()))
        psi[i] = [0.0] * n
    file.close()
    for i in range(n):
        total = 0
        for j in range(n):
            total += interactions[i][j]
        if total > 0:
            for j in range(n):
                psi[j][i] = min(1.0, round(influence * interactions[i][j] / total, precision))
    return psi

def read_edges_instance(file_name, influence, precision):
    file = open(file_name, "r")
    n, m = map(int, file.readline().split())
    edges = [None] * n
    psi = [None] * n
    for i in range(n):
        edges[i] = [0.0] * n
        psi[i] = [0.0] * n
    for i in range(m):
        line = list(map(int, file.readline().split()))
        edges[line[0] - 1][line[1] - 1] = line[2]
    file.close()
    for i in range(n):
        total = 0
        for j in range(n):
            total += edges[j][i]
        if total > 0:
            for j in range(n):
                psi[j][i] = min(1.0, round(influence * edges[j][i] / total, precision))
    return psi
        
def read_problem(file_name, psi_reader, influence = 2.0, precision = 2):
    epsilon = 0.1
    k = precision
    while k > 0:
        epsilon /= 10.0
        k -= 1
    psi = psi_reader('input/'+file_name + '.in', influence, precision)
    n = len(psi)
    alpha = [1.0] * n
    beta = [1.0] * n
    threshold = 1.0 / float(n)
    for i in range(n):
        infl = int(round(influence * sum(psi[i])))
        beta[i] += infl #reward: more influential indidividuals have greater rewards
        count = 0
        for j in range(n):
            if psi[j][i] >= threshold:
                count += 1
        infl_over = n - count #effort: individuals easelly influenced by others have lesser efforts
        alpha[i] += (infl_over + infl) // 3
    return {'alpha': alpha, 'beta': beta, 'psi': psi, 'epsilon': epsilon, 'k_max': int(round(sum(alpha) / influence)), 'k_min': int(round(sum(beta) / influence)) }

#pairs <file-name: reader-function>
file_function_map = {"knoke_bureaucracies_information": read_edges_instance,#1 instance size 10x10
                 "knoke_bureaucracies_money": read_edges_instance,#2 instance size 10x10
                 "roethlisberger_dickson_bank_wiring_room_gaming": read_transpose_interaction_instance,#3 instance size 14x14
                 "roethlisberger_dickson_bank_wiring_room_friendship": read_interaction_instance,#4 instance size 14x14
                 "roethlisberger_dickson_bank_wiring_room_helping": read_interaction_instance,#5 instance size 14x14
                 "kapferer_mine_one": read_interaction_instance,#6 instance size 15x15
                 "kapferer_mine_multiple": read_interaction_instance,#7 instance size 15x15
                 "thurman_office_formal":read_transpose_interaction_instance,#8 instance size 15x15
                 "thurman_office_informal": read_interaction_instance,#9 instance size 15x15
                 "newcomb_fraternity_1": read_ranking_instance,#10 instance size 17x17
                 "newcomb_fraternity_2": read_ranking_instance,#11 instance size 17x17
                 "newcomb_fraternity_3": read_ranking_instance,#12 instance size 17x17
                 "newcomb_fraternity_4": read_ranking_instance,#13 instance size 17x17
                 "newcomb_fraternity_5": read_ranking_instance,#14 instance size 17x17
                 "newcomb_fraternity_6": read_ranking_instance,#15 instance size 17x17
                 "newcomb_fraternity_7": read_ranking_instance,#16 instance size 17x17
                 "newcomb_fraternity_8": read_ranking_instance,#17 instance size 17x17
                 "newcomb_fraternity_9": read_ranking_instance,#18 instance size 17x17
                 "newcomb_fraternity_10": read_ranking_instance,#19 instance size 17x17
                 "newcomb_fraternity_11": read_ranking_instance,#20 instance size 17x17
                 "newcomb_fraternity_12": read_ranking_instance,#21 instance size 17x17
                 "newcomb_fraternity_13": read_ranking_instance,#22 instance size 17x17
                 "newcomb_fraternity_14": read_ranking_instance,#23 instance size 17x17
                 "newcomb_fraternity_15": read_ranking_instance,#24 instance size 17x17
                 "davis_southern_club_women": read_event_attendance_instance,#25 instance size 18x18
                 "sampson_monastery_like_1": read_transpose_interaction_instance,#26 instance size 18x18
                 "sampson_monastery_like_2": read_transpose_interaction_instance,#27 instance size 18x18
                 "sampson_monastery_like_3": read_transpose_interaction_instance,#28 instance size 18x18
                 "sampson_monastery_esteem": read_transpose_interaction_instance,#29 instance size 18x18
                 "sampson_monastery_influence": read_interaction_instance,#30 instance size 18x18
                 "krackhardt_office_css_advise_1": read_transpose_interaction_instance,#31 instance size 21x21
                 "krackhardt_office_css_advise_2": read_transpose_interaction_instance,#32 instance size 21x21
                 "krackhardt_office_css_advise_3": read_transpose_interaction_instance,#33 instance size 21x21
                 "krackhardt_office_css_advise_4": read_transpose_interaction_instance,#34 instance size 21x21
                 "krackhardt_office_css_advise_5": read_transpose_interaction_instance,#35 instance size 21x21
                 "krackhardt_office_css_advise_6": read_transpose_interaction_instance,#36 instance size 21x21
                 "krackhardt_office_css_advise_7": read_transpose_interaction_instance,#37 instance size 21x21
                 "krackhardt_office_css_advise_8": read_transpose_interaction_instance,#38 instance size 21x21
                 "krackhardt_office_css_advise_9": read_transpose_interaction_instance,#39 instance size 21x21
                 "krackhardt_office_css_advise_10": read_transpose_interaction_instance,#40 instance size 21x21
                 "krackhardt_office_css_advise_11": read_transpose_interaction_instance,#41 instance size 21x21
                 "krackhardt_office_css_advise_12": read_transpose_interaction_instance,#42 instance size 21x21
                 "krackhardt_office_css_advise_13": read_transpose_interaction_instance,#43 instance size 21x21
                 "krackhardt_office_css_advise_14": read_transpose_interaction_instance,#44 instance size 21x21
                 "krackhardt_office_css_advise_15": read_transpose_interaction_instance,#45 instance size 21x21
                 "krackhardt_office_css_advise_16": read_transpose_interaction_instance,#46 instance size 21x21
                 "krackhardt_office_css_advise_17": read_transpose_interaction_instance,#47 instance size 21x21
                 "krackhardt_office_css_advise_18": read_transpose_interaction_instance,#48 instance size 21x21
                 "krackhardt_office_css_advise_19": read_transpose_interaction_instance,#49 instance size 21x21
                 "krackhardt_office_css_advise_20": read_transpose_interaction_instance,#50 instance size 21x21
                 "krackhardt_office_css_advise_21": read_transpose_interaction_instance,#51 instance size 21x21
                 "krackhardt_office_css_friendship_1": read_transpose_interaction_instance,#52 instance size 21x21
                 "krackhardt_office_css_friendship_2": read_transpose_interaction_instance,#53 instance size 21x21
                 "krackhardt_office_css_friendship_3": read_transpose_interaction_instance,#54 instance size 21x21
                 "krackhardt_office_css_friendship_4": read_transpose_interaction_instance,#55 instance size 21x21
                 "krackhardt_office_css_friendship_5": read_transpose_interaction_instance,#56 instance size 21x21
                 "krackhardt_office_css_friendship_6": read_transpose_interaction_instance,#57 instance size 21x21
                 "krackhardt_office_css_friendship_7": read_transpose_interaction_instance,#58 instance size 21x21
                 "krackhardt_office_css_friendship_8": read_transpose_interaction_instance,#59 instance size 21x21
                 "krackhardt_office_css_friendship_9": read_transpose_interaction_instance,#60 instance size 21x21
                 "krackhardt_office_css_friendship_10": read_transpose_interaction_instance,#61 instance size 21x21
                 "krackhardt_office_css_friendship_11": read_transpose_interaction_instance,#62 instance size 21x21
                 "krackhardt_office_css_friendship_12": read_transpose_interaction_instance,#63 instance size 21x21
                 "krackhardt_office_css_friendship_13": read_transpose_interaction_instance,#64 instance size 21x21
                 "krackhardt_office_css_friendship_14": read_transpose_interaction_instance,#65 instance size 21x21
                 "krackhardt_office_css_friendship_15": read_transpose_interaction_instance,#66 instance size 21x21
                 "krackhardt_office_css_friendship_16": read_transpose_interaction_instance,#67 instance size 21x21
                 "krackhardt_office_css_friendship_17": read_transpose_interaction_instance,#68 instance size 21x21
                 "krackhardt_office_css_friendship_18": read_transpose_interaction_instance,#69 instance size 21x21
                 "krackhardt_office_css_friendship_19": read_transpose_interaction_instance,#70 instance size 21x21
                 "krackhardt_office_css_friendship_20": read_transpose_interaction_instance,#71 instance size 21x21
                 "krackhardt_office_css_friendship_21": read_transpose_interaction_instance,#72 instance size 21x21
		         "zachary_karate_club_binary": read_interaction_instance,#73 instance size 34x34
                 "zachary_karate_club_strength": read_interaction_instance,#74 instance size 34x34
                 "bernard_killworth_technical": read_interaction_instance,#75 instance size 34x34
                 "kapferer_tailor_shop_instrumental_1": read_interaction_instance,#76 instance size 39x39
                 "kapferer_tailor_shop_instrumental_2": read_interaction_instance,#77 instance size 39x39
                 "kapferer_tailor_shop_social_1": read_interaction_instance,#78 instance size 39x39
                 "kapferer_tailor_shop_social_2": read_interaction_instance,#79 instance size 39x39
                 "bernard_killworth_office": read_transpose_interaction_instance,#80 instance size 40x40
                 "bernard_killworth_ham_radio": read_transpose_interaction_instance,#81 instance size 44x44
                 "bernard_killworth_fraternity": read_transpose_interaction_instance#82 instance size 58x58
                 }

names = ['01. KNOKI.dat', '02. KNOKM.dat', '03. RDGAM.dat', '04. RDPOS.dat', '05. RDHLP.dat', '06. KAPFMU.dat', '07. KAPFMM.dat', '08. THURA.dat', '09. THURM.dat', '10. NEWC1.dat', '11. NEWC2.dat', '12. NEWC3.dat', '13. NEWC4.dat', '14. NEWC5.dat', '15. NEWC6.dat', '16. NEWC7.dat', '17. NEWC8.dat', '18. NEWC9.dat', '19. NEWC10.dat', '20. NEWC11.dat', '21. NEWC12.dat', '22. NEWC13.dat', '23. NEWC14.dat', '24. NEWC15.dat', '25. DAVIS.dat', '26. SAMPLK1.dat', '27. SAMPLK2.dat', '28. SAMPLK3.dat', '29. SAMPES.dat', '30. SAMPIN.dat', '31. KRACKAD1.dat', '32. KRACKAD2.dat', '33. KRACKAD3.dat', '34. KRACKAD4.dat', '35. KRACKAD5.dat', '36. KRACKAD6.dat', '37. KRACKAD7.dat', '38. KRACKAD8.dat', '39. KRACKAD9.dat', '40. KRACKAD10.dat', '41. KRACKAD11.dat', '42. KRACKAD12.dat', '43. KRACKAD13.dat', '44. KRACKAD14.dat', '45. KRACKAD15.dat', '46. KRACKAD16.dat', '47. KRACKAD17.dat', '48. KRACKAD18.dat', '49. KRACKAD19.dat', '50. KRACKAD20.dat', '51. KRACKAD21.dat', '52. KRACKFR1.dat', '53. KRACKFR2.dat', '54. KRACKFR3.dat', '55. KRACKFR4.dat', '56. KRACKFR5.dat', '57. KRACKFR6.dat', '58. KRACKFR7.dat', '59. KRACKFR8.dat', '60. KRACKFR9.dat', '61. KRACKFR10.dat', '62. KRACKFR11.dat', '63. KRACKFR12.dat', '64. KRACKFR13.dat', '65. KRACKFR14.dat', '66. KRACKFR15.dat', '67. KRACKFR16.dat', '68. KRACKFR17.dat', '69. KRACKFR18.dat', '70. KRACKFR19.dat', '71. KRACKFR20.dat', '72. KRACKFR21.dat', '73. ZACHE.dat', '74. ZACHC.dat', '75. BKTECC.dat', '76. KAPFTI1.dat', '77. KAPFTI2.dat', '78. KAPFTS1.dat', '79. KAPFTS2.dat', '80. BKOFFC.dat', '81. BKHAMC.dat', '82. BKFRAC.dat']

#reading all instances
i = 0
for file, function in file_function_map.items():
    problem = read_problem(file, function)

    f = open('output/'+str(names[i]), 'a')

    f.write('\n')
    f.write('PSI:\n')
    for x in problem['psi']:
        for y in x:
            f.write(str(y) + ' ')
        f.write('\n')

    f.write('\n')
    f.write('ALPHA:\n')
    for x in problem['alpha']:
        f.write(str(x) + ' ')

    f.write('\n')
    f.write('\n')
    f.write('BETA:\n')
    for x in problem['beta']:
        f.write(str(x) + ' ')

    f.write('\n')
    f.write('\n')
    f.write('K_MIN:\n')
    f.write(str(problem['k_min']))

    f.write('\n')
    f.write('\n')
    f.write('K_MAX:\n')
    f.write(str(problem['k_max']))

    i += 1
